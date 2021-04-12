package com.gbsoft.smartpatient.ui.main.chatlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.ChatIdentifier;
import com.gbsoft.smartpatient.databinding.ChatRowBinding;

public class ChatListViewHolder extends RecyclerView.ViewHolder {
    private final ChatRowBinding binding;

    public ChatListViewHolder(@NonNull ChatRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    static ChatListViewHolder create(ViewGroup parent) {
        return new ChatListViewHolder(ChatRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    void unbind() {
        binding.unbind();
    }

    void bindModel(ChatIdentifier model, boolean isHp) {
        String receiverInfo = (isHp ? model.getpUid() : model.getHpUid())
                + "_"
                + (isHp ? model.getpName() : model.getHpName());
        binding.setVm(new ChatItemVM(receiverInfo, model.getLastMsg()));
        binding.executePendingBindings();
    }
}
