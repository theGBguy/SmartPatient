package com.gbsoft.smartpatient.ui.main.chatdetails;

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;

import org.jetbrains.annotations.NotNull;


public class ChatDetailsListAdapter extends FirestoreRecyclerAdapter<Message, BaseChatDetailsVH> {
    private final RVEmptyObserver obv;
    private final String senderInfo;

    public ChatDetailsListAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RVEmptyObserver obv, String senderInfo) {
        super(options);
        this.obv = obv;
        this.senderInfo = senderInfo;
    }

    @Override
    public @NotNull BaseChatDetailsVH onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return BaseChatDetailsVH.create(parent, viewType);
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseChatDetailsVH holder, int position, @NonNull Message model) {
        holder.bindModel(model);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseChatDetailsVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        obv.onChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (TextUtils.equals(getItem(position).getSenderInfo(), senderInfo)) ? 2 : 1;
    }
}