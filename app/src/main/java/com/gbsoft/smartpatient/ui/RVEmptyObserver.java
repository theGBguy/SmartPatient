package com.gbsoft.smartpatient.ui;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    private final WeakReference<RecyclerView> recyclerView;
    private final WeakReference<View> emptyView;

    public RVEmptyObserver(RecyclerView recyclerView, View emptyView) {
        this.recyclerView = new WeakReference<>(recyclerView);
        this.emptyView = new WeakReference<>(emptyView);
        checkForEmptiness();
    }

    private void checkForEmptiness() {
        RecyclerView rv = recyclerView.get();
        if (rv != null && rv.getAdapter() != null) {
            boolean shouldShowEmptyView = rv.getAdapter().getItemCount() == 0;
            rv.setVisibility(shouldShowEmptyView ? View.GONE : View.VISIBLE);
            View ev = emptyView.get();
            if (ev != null)
                ev.setVisibility(shouldShowEmptyView ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onChanged() {
        super.onChanged();
        checkForEmptiness();
    }
}
