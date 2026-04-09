package com.example.dawak.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dawak.R;
import com.example.dawak.SessionManager;

public class SplashActivity extends BaseActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        checkPermissionAndProceed();
    }


    private void checkPermissionAndProceed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {

                goNext(); // عنده صلاحية → كمل

            } else {
                // اطلب الصلاحية
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }

        } else {
            goNext(); // الأجهزة القديمة ما تحتاج
        }
    }

    // 🚀 الانتقال
    private void goNext() {

        boolean isLoggedIn = sessionManager.isLoggedIn();

        if (isLoggedIn) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    // 📩 نتيجة الطلب
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "تم السماح بالإشعارات", Toast.LENGTH_SHORT).show();
                goNext();

            } else {
                Toast.makeText(this, "يجب السماح بالإشعارات للمتابعة", Toast.LENGTH_LONG).show();

                // 🔁 إعادة الطلب مرة ثانية
                checkPermissionAndProceed();
            }
        }
    }
}