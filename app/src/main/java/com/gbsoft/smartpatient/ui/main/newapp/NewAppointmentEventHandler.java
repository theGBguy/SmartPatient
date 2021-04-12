package com.gbsoft.smartpatient.ui.main.newapp;

import androidx.fragment.app.FragmentManager;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.time.Instant;
import java.util.Date;

public class NewAppointmentEventHandler {

    public void onSetAppointmentDateClick(NewAppointmentViewModel vm, FragmentManager manager) {
        SwitchDateTimeDialogFragment fragment = SwitchDateTimeDialogFragment.newInstance("Choose appointment time!",
                "Ok", "Cancel");
        fragment.startAtCalendarView();
        fragment.setMinimumDateTime(Date.from(Instant.now()));
        fragment.setDefaultDateTime(Date.from(Instant.now()));

        fragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                vm.setAppointmentTime(date);
                fragment.dismiss();
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                fragment.dismiss();
            }
        });

        fragment.setShowsDialog(true);
        fragment.show(manager, "Switch_Date_Time_Picker_Dialog");
    }

}
