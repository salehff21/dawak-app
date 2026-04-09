package com.example.dawak.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dawak.databinding.FragmentMedicinesListBinding;

public class MedicinesListFragment extends Fragment {

    private FragmentMedicinesListBinding binding;
    private com.example.dawak.DatabaseHelper dbHelper;
    private com.example.dawak.MedicinesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicinesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new com.example.dawak.DatabaseHelper(requireContext());
        
        // Setup RecyclerView
        binding.recyclerViewMeds.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        adapter = new com.example.dawak.MedicinesAdapter(dbHelper.getAllMedicines(), medicine -> {
            String newStatus = "Pending".equals(medicine.getStatus()) ? "Taken" : "Pending";
            dbHelper.updateMedicineStatus(medicine.getId(), newStatus);
            adapter.updateData(dbHelper.getAllMedicines());
        });
        binding.recyclerViewMeds.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && dbHelper != null) {
            adapter.updateData(dbHelper.getAllMedicines());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
