package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.databinding.PatientRowBinding;

public class PatientViewHolder extends RecyclerView.ViewHolder {
    private final PatientRowBinding binding;

    public PatientViewHolder(@NonNull PatientRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void unbind() {
        binding.unbind();
    }

    static PatientViewHolder create(ViewGroup parent) {
        return new PatientViewHolder(PatientRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    void bindModel(Patient patient, String onClickMode) {
        binding.setVm(new PatientVM(patient, onClickMode));
        binding.executePendingBindings();
    }
}
