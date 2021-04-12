package com.gbsoft.smartpatient.ui.main.chatdetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.databinding.ChatDetailsRowLeftBinding;

public class LeftVH extends BaseChatDetailsVH {
    private final ChatDetailsRowLeftBinding binding;

    LeftVH(ChatDetailsRowLeftBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    void unbind() {
        binding.unbind();
    }

    @Override
    void bindModel(Message msg) {
        binding.setMsg(msg.getContent());
        binding.setDate(msg.getMsgId());
        binding.executePendingBindings();
    }

    static LeftVH create(ViewGroup parent) {
        return new LeftVH(ChatDetailsRowLeftBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }
}
