package com.gbsoft.smartpatient.ui.main.home.online;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.databinding.MedicineOnlineRowBinding;

public class MedicineVH extends RecyclerView.ViewHolder {
    private final MedicineOnlineRowBinding binding;

    public MedicineVH(@NonNull MedicineOnlineRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void unbind() {
        binding.unbind();
    }

    static MedicineVH create(ViewGroup parent) {
        return new MedicineVH(MedicineOnlineRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    void bindModel(Medicine model, boolean isHp) {
        binding.setVm(new MedicineItemVm(model, isHp));
        binding.executePendingBindings();
    }
}
