package com.example.dawak.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dawak.R;
import com.example.dawak.databinding.ActivityAddMedicineBinding;

public class AddMedicineActivity extends BaseActivity {

    private ActivityAddMedicineBinding binding;
    private com.example.dawak.DatabaseHelper dbHelper;

    private String selectedDose = "5 mg";
    private List<String> selectedTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new com.example.dawak.DatabaseHelper(this);

        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(com.example.dawak.R.anim.fade_in, com.example.dawak.R.anim.slide_out_left);
        });

        // Setup Dose Selection
        setupDoseSelection();

        // Setup Time Picker
        binding.btnAddTime.setOnClickListener(v -> showTimePickerDialog());

        binding.btnSaveMedication.setOnClickListener(v -> saveMedication());
    }

    private void setupDoseSelection() {
        View.OnClickListener doseClickListener = v -> {
            // Reset all to unselected
            binding.tvDose5.setBackgroundResource(R.drawable.chip_unselected);
            binding.tvDose5.setTextColor(getResources().getColor(R.color.textColorSecondary));
            
            binding.tvDose10.setBackgroundResource(R.drawable.chip_unselected);
            binding.tvDose10.setTextColor(getResources().getColor(R.color.textColorSecondary));
            
            binding.tvDose20.setBackgroundResource(R.drawable.chip_unselected);
            binding.tvDose20.setTextColor(getResources().getColor(R.color.textColorSecondary));

            // Set selected
            TextView selectedView = (TextView) v;
            selectedView.setBackgroundResource(R.drawable.chip_selected);
            selectedView.setTextColor(getResources().getColor(R.color.colorPrimary));
            
            // Extract dose text cleanly (ignoring newlines if present, or just clean it)
            selectedDose = selectedView.getText().toString().replace("\n", " ");
        };

        binding.tvDose5.setOnClickListener(doseClickListener);
        binding.tvDose10.setOnClickListener(doseClickListener);
        binding.tvDose20.setOnClickListener(doseClickListener);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int formattedHour = hourOfDay % 12;
                    if (formattedHour == 0) formattedHour = 12; // 12 AM/PM

                    String timeString = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minuteOfHour, amPm);
                    addTimeView(timeString, hourOfDay);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void addTimeView(String timeString, int hourOfDay) {
        if (selectedTimes.contains(timeString)) {
            Toast.makeText(this, "Time already added", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedTimes.add(timeString);

        View timeView = LayoutInflater.from(this).inflate(R.layout.item_time, binding.llTimesContainer, false);
        TextView tvTimeValue = timeView.findViewById(R.id.tvTimeValue);
        ImageView btnDeleteTime = timeView.findViewById(R.id.btnDeleteTime);

        String periodLabel = (hourOfDay >= 5 && hourOfDay < 12) ? "Morning dose" :
                             (hourOfDay >= 12 && hourOfDay < 17) ? "Afternoon dose" :
                             (hourOfDay >= 17 && hourOfDay < 21) ? "Evening dose" : "Night dose";

        tvTimeValue.setText(timeString + "\n" + periodLabel);

        btnDeleteTime.setOnClickListener(v -> {
            binding.llTimesContainer.removeView(timeView);
            selectedTimes.remove(timeString);
        });

        binding.llTimesContainer.addView(timeView);
    }

    private void saveMedication() {
        String name = binding.etMedicineName.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();
        String status = "Pending";

        if (name.isEmpty() || selectedTimes.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_empty_fields) + " or add at least one time.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for conflicts first
        List<String> conflictedTimes = new ArrayList<>();
        for (String time : selectedTimes) {
            if (dbHelper.checkTimeConflict(time)) {
                conflictedTimes.add(time);
            }
        }

        if (!conflictedTimes.isEmpty()) {
            Toast.makeText(this, "Conflict detected for: " + conflictedTimes.toString(), Toast.LENGTH_LONG).show();
            return; // Abort saving if ANY conflict exists
        }

        // Insert all individual times as distinct records
        boolean allSuccess = true;
        for (String time : selectedTimes) {
            com.example.dawak.Medicine newMed = new com.example.dawak.Medicine(name, selectedDose, time, notes, status);
            if (dbHelper.insertMedicine(newMed)) {
                scheduleLocalAlarm(name, time);
            } else {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            Toast.makeText(this, getString(R.string.medicine_added_success), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving some medicine schedules.", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleLocalAlarm(String medName, String timeStr) {
        // Parse time format (e.g. 08:00 AM)
        String[] parts = timeStr.split("[: ]");
        if (parts.length < 3) return;
        
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String amAm = parts[2];
        
        if (amAm.equalsIgnoreCase("PM") && hour < 12) hour += 12;
        if (amAm.equalsIgnoreCase("AM") && hour == 12) hour = 0;

        // Set Main Alarm Time
        Calendar mainCalendar = Calendar.getInstance();
        mainCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mainCalendar.set(Calendar.MINUTE, minute);
        mainCalendar.set(Calendar.SECOND, 0);

        if (mainCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
            mainCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Set Pre-reminder Time (5 minutes prior)
        Calendar preCalendar = (Calendar) mainCalendar.clone();
        preCalendar.add(Calendar.MINUTE, -5);

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
        
        // 1. Schedule Pre-Reminder
        android.content.Intent preIntent = new android.content.Intent(this, com.example.dawak.AlarmReceiver.class);
        preIntent.putExtra("MED_NAME", medName);
        preIntent.putExtra("IS_PRE_REMINDER", true);
        
        int preReqCode = (int) System.currentTimeMillis() + hour * 60 + minute - 5;
        android.app.PendingIntent prePending = android.app.PendingIntent.getBroadcast(this, preReqCode, preIntent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        
        // 2. Schedule Main Reminder
        android.content.Intent mainIntent = new android.content.Intent(this, com.example.dawak.AlarmReceiver.class);
        mainIntent.putExtra("MED_NAME", medName);
        mainIntent.putExtra("IS_PRE_REMINDER", false);
        
        int mainReqCode = (int) System.currentTimeMillis() + hour * 60 + minute + 5; 
        android.app.PendingIntent mainPending = android.app.PendingIntent.getBroadcast(this, mainReqCode, mainIntent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

        // Deploy RTC WAKEUP EXACT to OS
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, preCalendar.getTimeInMillis(), prePending);
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, mainCalendar.getTimeInMillis(), mainPending);
        } else {
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, preCalendar.getTimeInMillis(), prePending);
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, mainCalendar.getTimeInMillis(), mainPending);
        }
    }
}
