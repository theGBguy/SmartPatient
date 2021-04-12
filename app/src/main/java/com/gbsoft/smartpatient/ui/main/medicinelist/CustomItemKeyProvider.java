package com.gbsoft.smartpatient.ui.main.medicinelist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

public class CustomItemKeyProvider extends ItemKeyProvider<Long> {
    private final RecyclerView recyclerView;

    protected CustomItemKeyProvider(RecyclerView recyclerView) {
        super(SCOPE_MAPPED);
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return recyclerView.getAdapter().getItemId(position);
    }

    @Override
    public int getPosition(@NonNull Long key) {
        CustomViewHolder viewHolder = (CustomViewHolder) recyclerView.findViewHolderForItemId(key);
        return viewHolder == null ? RecyclerView.NO_POSITION : viewHolder.getLayoutPosition();
    }
}
