package com.example.dawak;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "dawak_reminders_hq";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("MED_NAME");
        if (medicineName == null) medicineName = "your medication";
        
        boolean isPreReminder = intent.getBooleanExtra("IS_PRE_REMINDER", false);
        String noticeTitle = isPreReminder ? "Medicine Incoming!" : "Medicine Reminder";
        String noticeBody = isPreReminder ? 
                "Your " + medicineName + " is coming in 5 minutes." : 
                "It's time to take " + medicineName + "!";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Required for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "High Priority Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Critical Audio-Enabled alerts for Medications");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            
            android.media.AudioAttributes audioAttributes = new android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                    .build();
                    
            channel.setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM), audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(noticeTitle)
                .setContentText(noticeBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM))
                .setVibrate(new long[]{0, 500, 200, 500})
                .setAutoCancel(true);

        // Full Screen Intent optionally escalates priority globally
        android.content.Intent emptyIntent = new android.content.Intent();
        android.app.PendingIntent fullScreenPending = android.app.PendingIntent.getActivity(context, 0, emptyIntent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        builder.setFullScreenIntent(fullScreenPending, true);

        // Pseudo-random ID allows distinct overlaps safely
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}
