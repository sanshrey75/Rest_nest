package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    EditText etLoginEmail, etLoginPassword;
    Button btnSignIn;
    TextView tvSignUpRedirect;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign In");
        }

        mAuth = FirebaseAuth.getInstance();

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUpRedirect = findViewById(R.id.tvSignUpRedirect);

        btnSignIn.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvSignUpRedirect.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
