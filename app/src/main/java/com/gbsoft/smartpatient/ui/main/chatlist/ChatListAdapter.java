package com.gbsoft.smartpatient.ui.main.chatlist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.ChatIdentifier;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;

import org.jetbrains.annotations.NotNull;

public class ChatListAdapter extends FirestoreRecyclerAdapter<ChatIdentifier, ChatListViewHolder> {
    private final RVEmptyObserver obv;
    private final boolean isHp;

    public ChatListAdapter(@NonNull FirestoreRecyclerOptions<ChatIdentifier> options, RVEmptyObserver obv, boolean isHp) {
        super(options);
        this.obv = obv;
        this.isHp = isHp;
    }

    @Override
    public @NotNull ChatListViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return ChatListViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatListViewHolder holder, int position, @NonNull ChatIdentifier model) {
        holder.bindModel(model, isHp);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ChatListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        obv.onChanged();
    }
}