package com.example.dawak.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dawak.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private com.example.dawak.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new com.example.dawak.DatabaseHelper(this);

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etNameReg.getText().toString().trim();
            String email = binding.etEmailReg.getText().toString().trim();
            String password = binding.etPasswordReg.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, getString(com.example.dawak.R.string.fill_empty_fields), android.widget.Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.registerUser(name, email, password)) {
                    android.widget.Toast.makeText(this, "Account Created Successfully", android.widget.Toast.LENGTH_SHORT).show();
                    
                    // Save Session
                    com.example.dawak.SessionManager sessionManager = new com.example.dawak.SessionManager(RegisterActivity.this);
                    sessionManager.createLoginSession(email);

                    // Navigate to Dashboard
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    overridePendingTransition(com.example.dawak.R.anim.slide_in_right, com.example.dawak.R.anim.slide_out_left);
                    finishAffinity();
                } else {
                    android.widget.Toast.makeText(this, "Registration Failed", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvSignIn.setOnClickListener(v -> {
            // Go back to Login
            finish();
            overridePendingTransition(com.example.dawak.R.anim.fade_in, com.example.dawak.R.anim.fade_out);
        });
    }
}
