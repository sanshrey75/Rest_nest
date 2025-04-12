package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    EditText etLoginEmail, etLoginPassword;
    Button btnSignIn;
    TextView tvSignUpRedirect;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Enable back button in ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign In");
        }

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUpRedirect = findViewById(R.id.tvSignUpRedirect);

        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        btnSignIn.setOnClickListener(v -> {
            String inputEmail = etLoginEmail.getText().toString().trim();
            String inputPassword = etLoginPassword.getText().toString().trim();

            String storedEmail = sharedPreferences.getString(SignUpActivity.KEY_EMAIL, null);
            String storedPassword = sharedPreferences.getString(SignUpActivity.KEY_PASSWORD, null);

            if (inputEmail.equals(storedEmail) && inputPassword.equals(storedPassword)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
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
