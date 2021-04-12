package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.databinding.MedicineRowBinding;

import org.jetbrains.annotations.NotNull;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private final MedicineRowBinding binding;

    CustomViewHolder(MedicineRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    static CustomViewHolder create(ViewGroup parent) {
        return new CustomViewHolder(MedicineRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    void bindModel(final ReminderWithMedicine remWithMed, final boolean isActivated) {
        binding.setVm(new ItemViewModel(remWithMed, isActivated));
        binding.executePendingBindings();
    }

    void unbind() {
        binding.unbind();
    }

    final ItemDetailsLookup.ItemDetails<Long> getItemDetails = new ItemDetailsLookup.ItemDetails<Long>() {
        @Override
        public int getPosition() {
            return getAdapterPosition();
        }

        @Override
        public @NotNull Long getSelectionKey() {
            return getItemId();
        }
    };
}
