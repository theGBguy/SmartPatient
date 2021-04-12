package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;

public class HealthPersonnelListAdapter extends FirestoreRecyclerAdapter<HealthPersonnel, HealthPersonnelVH> {
    private final RVEmptyObserver obv;
    private final String onClickMode;

    public HealthPersonnelListAdapter(@NonNull FirestoreRecyclerOptions<HealthPersonnel> options, RVEmptyObserver obv, String onClickMode) {
        super(options);
        this.obv = obv;
        this.onClickMode = onClickMode;
    }

    @Override
    protected void onBindViewHolder(@NonNull HealthPersonnelVH holder, int position, @NonNull HealthPersonnel model) {
        holder.bindModel(model, onClickMode);
    }

    @NonNull
    @Override
    public HealthPersonnelVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return HealthPersonnelVH.create(parent);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        obv.onChanged();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull HealthPersonnelVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }
}
