package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.databinding.HealthPersonnelRowBinding;

public class HealthPersonnelVH extends RecyclerView.ViewHolder {
    private final HealthPersonnelRowBinding binding;

    public HealthPersonnelVH(@NonNull HealthPersonnelRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void unbind() {
        binding.unbind();
    }

    static HealthPersonnelVH create(ViewGroup parent) {
        return new HealthPersonnelVH(HealthPersonnelRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    void bindModel(HealthPersonnel model, String onClickMode) {
        binding.setVm(new HealthPersonnelVM(model, onClickMode));
        binding.executePendingBindings();
    }
}
