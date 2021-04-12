package com.gbsoft.smartpatient.ui.main.chatdetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.databinding.ChatDetailsRowPhotoBinding;

public class PhotoVH extends BaseChatDetailsVH {
    private final ChatDetailsRowPhotoBinding binding;

    PhotoVH(ChatDetailsRowPhotoBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    void bindModel(Message photo) {
        binding.setImgUrl(photo.getContent());
        binding.executePendingBindings();
    }

    @Override
    void unbind() {
        binding.unbind();
    }

    static PhotoVH create(ViewGroup parent) {
        return new PhotoVH(ChatDetailsRowPhotoBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }
}
