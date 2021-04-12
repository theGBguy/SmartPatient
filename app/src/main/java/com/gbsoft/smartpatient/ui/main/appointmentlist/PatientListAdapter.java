package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;

public class PatientListAdapter extends FirestoreRecyclerAdapter<Patient, PatientViewHolder> {
    private final RVEmptyObserver obv;
    private final String onClickMode;

    public PatientListAdapter(@NonNull FirestoreRecyclerOptions<Patient> options, RVEmptyObserver obv, String onClickMode) {
        super(options);
        this.obv = obv;
        this.onClickMode = onClickMode;
    }

    @Override
    protected void onBindViewHolder(@NonNull PatientViewHolder holder, int position, @NonNull Patient model) {
        holder.bindModel(model, onClickMode);
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return PatientViewHolder.create(parent);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        obv.onChanged();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PatientViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }
}
