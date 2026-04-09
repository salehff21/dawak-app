package com.example.dawak.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dawak.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private com.example.dawak.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new com.example.dawak.DatabaseHelper(this);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, getString(com.example.dawak.R.string.fill_empty_fields), android.widget.Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkUser(email, password)) {
                // Save Session
                com.example.dawak.SessionManager sessionManager = new com.example.dawak.SessionManager(LoginActivity.this);
                sessionManager.createLoginSession(email);

                // Navigate to Dashboard
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                overridePendingTransition(com.example.dawak.R.anim.slide_in_right, com.example.dawak.R.anim.slide_out_left);
                finish();
            } else {
                android.widget.Toast.makeText(this, "Login Failed. Check credentials.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCreateAccount.setOnClickListener(v -> {
            // Navigate to Register
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(com.example.dawak.R.anim.fade_in, com.example.dawak.R.anim.fade_out);
        });
    }
}
