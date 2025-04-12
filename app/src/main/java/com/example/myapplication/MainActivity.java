package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnGoToSignUp, btnGoToSignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, redirect to HomeActivity
            redirectToHome();
            return;
        }

        // Initialize buttons only if user is not logged in
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        btnGoToSignIn = findViewById(R.id.btnGoToSignIn);

        btnGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });

        btnGoToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check authentication again when activity resumes
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToHome();
        }
    }

    private void redirectToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish MainActivity so user can't go back
    }
}