package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;

import org.jetbrains.annotations.NotNull;

public class AppointmentListAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentVH> {
    private final boolean isHp;
    private final RVEmptyObserver obv;
    private final ClickObserver observer;

    public AppointmentListAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options, boolean isHp, RVEmptyObserver obv, ClickObserver observer) {
        super(options);
        this.isHp = isHp;
        this.obv = obv;
        this.observer = observer;
    }

    @Override
    public @NotNull AppointmentVH onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return AppointmentVH.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull AppointmentVH holder, int position, @NonNull Appointment model) {
        holder.bindModel(model, isHp, observer);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AppointmentVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        obv.onChanged();
    }
}