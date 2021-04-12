package com.gbsoft.smartpatient.ui.main.medicinedetails;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.utils.TimeHelper;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

public class ReminderChip extends Chip {

    public ReminderChip(Context context, Reminder reminder) {
        super(context);
        customizeChip(reminder);
    }

    public ReminderChip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReminderChip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void customizeChip(@NotNull Reminder reminder) {
        this.setText(TimeHelper.formatLocalDateTime(reminder.getReminderTime()));
        String reminderType = reminder.getReminderType();
        if (TextUtils.equals(reminderType, "Pending")) {
            this.setChipBackgroundColorResource(R.color.colorErrorAlternate);
            this.setTextColor(getResources().getColor(R.color.lightDark));
        } else if (TextUtils.equals(reminderType, "Completed")) {
            this.setChipBackgroundColorResource(R.color.colorSuccess);
            this.setTextColor(getResources().getColor(R.color.lightWhite));
        } else {
            this.setChipBackgroundColorResource(R.color.colorError);
            this.setTextColor(getResources().getColor(R.color.lightWhite));
        }
    }
}
