package com.gbsoft.smartpatient.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.AlarmTimePickerBinding;
import com.gbsoft.smartpatient.ui.main.addupdatemed.AddUpdateViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogUtils {

    public static void showLogoutDialog(Context context, NavController navController) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_logout_24);
        icon = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, context.getResources().getColor(R.color.lightDark));

        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                .setIcon(icon)
                .setTitle(R.string.menuLogOut_title)
                .setMessage(R.string.dialog_logout_msg)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    dialog.dismiss();
                    navController.navigate(R.id.nav_login);
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static AlertDialog getUploadPicDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                .setIcon(R.drawable.ic_baseline_account_box_24)
                .setTitle(R.string.pic_upload)
                .setView(new CircularProgressIndicator(context))
                .setCancelable(false)
                .create();
    }

    public static void getAlarmTimePickerDialog(Context context, AddUpdateViewModel vm) {
        AlarmTimePickerBinding pickerBinding = AlarmTimePickerBinding.inflate(LayoutInflater.from(context), null, false);

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(pickerBinding.getRoot());
        dialog.show();

        List<LocalDate> selectedDates = new ArrayList<>();
        pickerBinding.dateRangePicker.setCalendarListener(new CalendarListener() {
            @Override
            public void onFirstDateSelected(@NotNull Calendar calendar) {
                selectedDates.clear();
                selectedDates.add(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate());
            }

            @Override
            public void onDateRangeSelected(@NotNull Calendar calendar, @NotNull Calendar calendar1) {
                selectedDates.clear();
                while (!calendar.after(calendar1)) {
                    selectedDates.add(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        });

        List<LocalTime> selectedTimes = new ArrayList<>();
        pickerBinding.chipAdd.setOnClickListener(v -> {
            Chip newChip = new Chip(context);
            LocalTime current = LocalTime.now();
            selectedTimes.add(current);
            newChip.setText(TimeHelper.formatLocalTime(current));
            newChip.setTag(current);
            newChip.setCloseIconVisible(true);
            newChip.setOnCloseIconClickListener(v1 -> {
                pickerBinding.chipGroupTime.removeView(newChip);
                selectedTimes.remove(newChip.getTag());
            });

            newChip.setOnClickListener(v12 -> {
                selectedTimes.remove(newChip.getTag());
                final TimePickerDialog dialog1 = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                    LocalTime selected = LocalTime.of(hourOfDay, minute);
                    newChip.setText(TimeHelper.formatLocalTime(selected));
                    newChip.setTag(selected);
                    selectedTimes.add(selected);
                }, LocalTime.now().getHour(), LocalTime.now().getMinute(), false);
                dialog1.show();
            });

            pickerBinding.chipGroupTime.addView(newChip);
        });

        pickerBinding.btnAlarmPickerOk.setOnClickListener(v -> {
                    dialog.dismiss();
                    vm.setNewReminderTimeList(selectedDates, selectedTimes);
                }
        );
    }
}
