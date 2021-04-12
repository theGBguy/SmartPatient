package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLookup extends ItemDetailsLookup<Long> {
    private final RecyclerView recyclerView;

    public CustomLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            return ((CustomViewHolder) recyclerView.getChildViewHolder(view)).getItemDetails;
        }
        return null;
    }
}
