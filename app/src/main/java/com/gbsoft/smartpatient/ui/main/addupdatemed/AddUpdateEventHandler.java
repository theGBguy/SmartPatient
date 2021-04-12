package com.gbsoft.smartpatient.ui.main.addupdatemed;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;

import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.utils.DialogUtils;

import java.time.LocalDate;

public class AddUpdateEventHandler {

    public void onSetReminderTimeClick(View v, AddUpdateViewModel vm) {
//        Navigation.findNavController(v).navigate(R.id.nav_reminder_date_time_picker);
        DialogUtils.getAlarmTimePickerDialog(v.getContext(), vm);
    }

    public void onSetExpiryDateClick(Context context, AddUpdateViewModel viewModel) {
        LocalDate now = LocalDate.now();
        final DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            viewModel.setExpiryDate(LocalDate.of(year, month, dayOfMonth));
        }, now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        dialog.show();

        LocalDate localDate = viewModel.getExpiryDateOrig();
        if (localDate != null)
            dialog.updateDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    public void onCancelClick(View v) {
        Navigation.findNavController(v).navigateUp();
    }
}
