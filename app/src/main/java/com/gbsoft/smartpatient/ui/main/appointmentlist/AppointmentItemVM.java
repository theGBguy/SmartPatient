package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.databinding.BindingAdapter;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Appointment;

public class AppointmentItemVM {
    private final Appointment appointment;
    private final boolean isHp;
    private final ClickObserver observer;

    public AppointmentItemVM(Appointment appointment, boolean isHp, ClickObserver observer) {
        this.appointment = appointment;
        this.isHp = isHp;
        this.observer = observer;
    }

    public boolean shouldHideButtons() {
        if (!isHp) return true;
        return appointment.isApproved() != null && appointment.isApproved();
    }

    public String getAppWithLabel() {
        if (isHp) {
            return "Appointment with Patient : " + appointment.getPatientName();
        } else {
            return "Appointment with Health Personnel : " + appointment.getHpName();
        }
    }

    public String getApprovedStatusLabel() {
        if (appointment.isApproved() == null)
            return "Waiting for approval";
        if (appointment.isApproved())
            return "Approved";
        return "Rejected";
    }

    public int getBgColor() {
        if (appointment.isApproved() == null)
            return R.color.colorErrorAlternate;
        if (appointment.isApproved())
            return R.color.colorSuccess;
        return R.color.colorError;
    }

    @BindingAdapter("bgColor")
    public static void setBgColor(TextView tv, @ColorRes int color) {
        tv.setBackgroundResource(color);
    }

    public void setApprovalStatus(boolean isApproved) {
        observer.onAppointmentStatusUpdate(appointment.getId(), isApproved);
    }

    public Appointment getAppointment() {
        return appointment;
    }
}
