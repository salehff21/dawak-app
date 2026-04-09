package com.example.dawak.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dawak.databinding.FragmentRemindersBinding;

public class RemindersFragment extends Fragment {

    private FragmentRemindersBinding binding;
    private com.example.dawak.DatabaseHelper dbHelper;
    private com.example.dawak.MedicinesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new com.example.dawak.DatabaseHelper(requireContext());

        binding.recyclerViewReminders.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        adapter = new com.example.dawak.MedicinesAdapter(getPendingMedicines(), medicine -> {
            String newStatus = "Pending".equals(medicine.getStatus()) ? "Taken" : "Pending";
            dbHelper.updateMedicineStatus(medicine.getId(), newStatus);
            adapter.updateData(getPendingMedicines());
        });
        binding.recyclerViewReminders.setAdapter(adapter);

        return root;
    }

    private java.util.List<com.example.dawak.Medicine> getPendingMedicines() {
        java.util.List<com.example.dawak.Medicine> all = dbHelper.getAllMedicines();
        java.util.List<com.example.dawak.Medicine> pending = new java.util.ArrayList<>();
        for (com.example.dawak.Medicine m : all) {
            if (!"Taken".equalsIgnoreCase(m.getStatus())) {
                pending.add(m);
            }
        }
        return pending;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && dbHelper != null) {
            adapter.updateData(getPendingMedicines());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
