package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.*;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnSignUp, btnGoogleSignUp;
    TextView tvLoginRedirect;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign Up");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignUp.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(name, email);

                            db.collection("users").document(uid).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, SignInActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });

        btnGoogleSignUp.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Save Google user to Firestore as well
                        if (user != null) {
                            User googleUser = new User(user.getDisplayName(), user.getEmail());
                            db.collection("users").document(user.getUid()).set(googleUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Signed in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}