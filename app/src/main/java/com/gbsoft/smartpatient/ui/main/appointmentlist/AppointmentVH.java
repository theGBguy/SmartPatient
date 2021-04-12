package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.databinding.AppointmentRowBinding;

public class AppointmentVH extends RecyclerView.ViewHolder {
    private final AppointmentRowBinding binding;

    public AppointmentVH(@NonNull AppointmentRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void unbind() {
        binding.unbind();
    }

    static AppointmentVH create(ViewGroup parent) {
        return new AppointmentVH(AppointmentRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    void bindModel(Appointment model, boolean isHp, ClickObserver observer) {
        binding.setVm(new AppointmentItemVM(model, isHp, observer));
        binding.executePendingBindings();
    }
}
