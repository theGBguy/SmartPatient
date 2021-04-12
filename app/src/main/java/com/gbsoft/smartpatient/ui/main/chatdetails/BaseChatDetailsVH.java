package com.gbsoft.smartpatient.ui.main.chatdetails;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpatient.data.Message;

import org.jetbrains.annotations.NotNull;

public abstract class BaseChatDetailsVH extends RecyclerView.ViewHolder {
    BaseChatDetailsVH(@NonNull View itemView) {
        super(itemView);
    }

    @NotNull
    static BaseChatDetailsVH create(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return LeftVH.create(parent);
            case 3:
                return PhotoVH.create(parent);
            default:
                return RightVH.create(parent);
        }
    }

    abstract void bindModel(Message msg);

    abstract void unbind();
}
