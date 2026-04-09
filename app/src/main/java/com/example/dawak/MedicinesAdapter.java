package com.example.dawak;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList;
    private OnMedicineStatusClickListener listener;

    public interface OnMedicineStatusClickListener {
        void onStatusClick(Medicine medicine);
    }

    public MedicinesAdapter(List<Medicine> medicineList, OnMedicineStatusClickListener listener) {
        this.medicineList = medicineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvMedicineName.setText(medicine.getName());
        holder.tvMedicineTime.setText(medicine.getTime());
        holder.tvMedicineDose.setText(medicine.getDose());
        
        // Setup status
        if (medicine.getStatus() != null && !medicine.getStatus().isEmpty()) {
            holder.tvMedicineStatus.setText(medicine.getStatus());
            if (medicine.getStatus().equalsIgnoreCase("Taken")) {
                holder.tvMedicineStatus.setBackgroundResource(R.drawable.btn_success);
                holder.tvMedicineStatus.setTextColor(0xFFFFFFFF); // White text
            } else {
                holder.tvMedicineStatus.setBackgroundResource(R.drawable.chip_selected);
                holder.tvMedicineStatus.setTextColor(0xFF6200EA); // Primary color
            }
        } else {
            holder.tvMedicineStatus.setText("Pending");
            holder.tvMedicineStatus.setBackgroundResource(R.drawable.chip_selected);
            holder.tvMedicineStatus.setTextColor(0xFF6200EA);
        }

        holder.tvMedicineStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusClick(medicine);
            }
        });
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

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedicineName;
        TextView tvMedicineTime;
        TextView tvMedicineDose;
        TextView tvMedicineStatus;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvMedicineTime = itemView.findViewById(R.id.tvMedicineTime);
            tvMedicineDose = itemView.findViewById(R.id.tvMedicineDose);
            tvMedicineStatus = itemView.findViewById(R.id.tvMedicineStatus);
        }
    }
}
