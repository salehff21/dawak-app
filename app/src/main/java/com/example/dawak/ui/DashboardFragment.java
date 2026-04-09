package com.example.dawak.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dawak.DashboardMedicinesAdapter;
import com.example.dawak.DatabaseHelper;
import com.example.dawak.Medicine;
import com.example.dawak.databinding.FragmentDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DatabaseHelper dbHelper;
    private DashboardMedicinesAdapter adapter;
    private com.example.dawak.SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new com.example.dawak.SessionManager(requireContext());

        // Header Updates
        updateHeader();

        // Setup the RecyclerView
        binding.recyclerViewDashboardMeds.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DashboardMedicinesAdapter(new ArrayList<>(), medicine -> {
            dbHelper.updateMedicineStatus(medicine.getId(), "Taken");
            refreshDashboardData(); // Instantly update lists and progress bar
        });
        binding.recyclerViewDashboardMeds.setAdapter(adapter);

        binding.fabAddMedicine.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddMedicineActivity.class));
            if (getActivity() != null) {
                getActivity().overridePendingTransition(com.example.dawak.R.anim.slide_in_right, com.example.dawak.R.anim.fade_out);
            }
        });

        // Language Switch Setup
        String currentLang = sessionManager.getLanguage();
        binding.btnLanguageSwitch.setText(currentLang.equalsIgnoreCase("ar") ? "EN" : "AR");

        binding.btnLanguageSwitch.setOnClickListener(v -> {
            String newLang = currentLang.equalsIgnoreCase("ar") ? "en" : "ar";
            sessionManager.saveLanguage(newLang);
            
            // Force Reload Activity
            if(getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(com.example.dawak.R.anim.fade_in, com.example.dawak.R.anim.fade_out);
            }
        });

        return root;
    }

    private void updateHeader() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 5 && timeOfDay < 12) {
            binding.tvGreeting.setText("Good Morning");
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            binding.tvGreeting.setText("Good Afternoon");
        } else {
            binding.tvGreeting.setText("Good Evening");
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        binding.tvCurrentTime.setText(timeFormat.format(new Date()));
    }

    private void refreshDashboardData() {
        if (dbHelper == null) return;

        List<Medicine> allMeds = dbHelper.getAllMedicines();
        
        // Let's assume all fetched medicines are "Today's" since we don't store actual dates natively yet.
        int total = allMeds.size();
        int taken = 0;
        
        List<Medicine> dashboardList = new ArrayList<>();

        for (Medicine m : allMeds) {
            // Include everything in the Dashboard list.
            dashboardList.add(m);
            if ("Taken".equalsIgnoreCase(m.getStatus())) {
                taken++;
            }
        }

        // Update Adherence Progress
        binding.tvAdherenceProgress.setText("You've taken " + taken + " of " + total + " doses today");
        if (total > 0) {
            int progress = (int) (((double) taken / total) * 100);
            binding.progressBarAdherence.setProgress(progress);
        } else {
            binding.progressBarAdherence.setProgress(0);
        }

        // Update RecyclerView
        if (adapter != null) {
            adapter.updateData(dashboardList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeader();           // Update time strictly when returning
        refreshDashboardData();   // Refresh from database
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
