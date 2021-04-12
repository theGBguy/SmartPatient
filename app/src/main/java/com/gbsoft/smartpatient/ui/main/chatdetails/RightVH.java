package com.gbsoft.smartpatient.ui.main.chatdetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.databinding.ChatDetailsRowRightBinding;

public class RightVH extends BaseChatDetailsVH {
    private final ChatDetailsRowRightBinding binding;

    RightVH(ChatDetailsRowRightBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    void bindModel(Message msg) {
        binding.setMsg(msg.getContent());
        binding.setDate(msg.getMsgId());
        binding.executePendingBindings();
    }

    @Override
    void unbind() {
        binding.unbind();
    }

    static RightVH create(ViewGroup parent) {
        return new RightVH(ChatDetailsRowRightBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }
}
