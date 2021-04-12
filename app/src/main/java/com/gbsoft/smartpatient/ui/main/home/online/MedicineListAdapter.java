package com.gbsoft.smartpatient.ui.main.home.online;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;

public class MedicineListAdapter extends FirestoreRecyclerAdapter<Medicine, MedicineVH> {
    private final boolean isHp;
    private final RVEmptyObserver obv;

    public MedicineListAdapter(@NonNull FirestoreRecyclerOptions<Medicine> options, boolean isHp, RVEmptyObserver obv) {
        super(options);
        this.isHp = isHp;
        this.obv = obv;
    }

    @Override
    protected void onBindViewHolder(@NonNull MedicineVH holder, int position, @NonNull Medicine model) {
        holder.bindModel(model, isHp);
    }

    @NonNull
    @Override
    public MedicineVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MedicineVH.create(parent);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MedicineVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        obv.onChanged();
    }
}