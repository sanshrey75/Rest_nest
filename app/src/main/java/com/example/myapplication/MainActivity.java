package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnGoToSignUp, btnGoToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        btnGoToSignIn = findViewById(R.id.btnGoToSignIn);

        btnGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });

        btnGoToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        });
    }
}
