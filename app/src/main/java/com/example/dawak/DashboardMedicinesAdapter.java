package com.example.dawak;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardMedicinesAdapter extends RecyclerView.Adapter<DashboardMedicinesAdapter.DashboardMedViewHolder> {

    private List<Medicine> medicineList;
    private OnLogDoseClickListener listener;

    public interface OnLogDoseClickListener {
        void onLogDose(Medicine medicine);
    }

    public DashboardMedicinesAdapter(List<Medicine> medicineList, OnLogDoseClickListener listener) {
        this.medicineList = medicineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DashboardMedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_medicine, parent, false);
        return new DashboardMedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardMedViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvMedName.setText(medicine.getName());
        holder.tvMedTime.setText(medicine.getTime());
        holder.tvMedDose.setText(medicine.getDose());

        // Toggle visibility between "Log Dose" button and "Taken" indicator safely
        if ("Taken".equalsIgnoreCase(medicine.getStatus())) {
            holder.btnLogDose.setVisibility(View.GONE);
            holder.tvTakenIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.btnLogDose.setVisibility(View.VISIBLE);
            holder.tvTakenIndicator.setVisibility(View.GONE);
            
            holder.btnLogDose.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLogDose(medicine);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return medicineList != null ? medicineList.size() : 0;
    }

    public void updateData(List<Medicine> newMedicines) {
        this.medicineList.clear();
        this.medicineList.addAll(newMedicines);
        notifyDataSetChanged();
    }

    public static class DashboardMedViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedName, tvMedTime, tvMedDose, tvTakenIndicator;
        Button btnLogDose;

        public DashboardMedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedName = itemView.findViewById(R.id.tvMedName);
            tvMedTime = itemView.findViewById(R.id.tvMedTime);
            tvMedDose = itemView.findViewById(R.id.tvMedDose);
            btnLogDose = itemView.findViewById(R.id.btnLogDose);
            tvTakenIndicator = itemView.findViewById(R.id.tvTakenIndicator);
        }
    }
}
