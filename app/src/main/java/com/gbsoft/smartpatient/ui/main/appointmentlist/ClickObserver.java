package com.gbsoft.smartpatient.ui.main.appointmentlist;

public interface ClickObserver {
    void onAppointmentStatusUpdate(long appId, boolean isApproved);
}
