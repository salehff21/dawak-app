package com.example.dawak.ui;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dawak.LocaleHelper;
import com.example.dawak.SessionManager;

public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager sessionManager = new SessionManager(newBase);
        String language = sessionManager.getLanguage();
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language));
    }
}
