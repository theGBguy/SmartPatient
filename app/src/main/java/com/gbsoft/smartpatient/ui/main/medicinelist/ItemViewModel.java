package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.utils.TimeHelper;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public class ItemViewModel {
    // medicine name, daily intake, reminder time, remaining time, image path
    private final String medName;
    private final int dailyIntake;
    private final LocalDateTime reminderTime;
    private final String imgPath;
    private final boolean isActivated;
    private final boolean isPending;

    public ItemViewModel(ReminderWithMedicine remWithMed, boolean isActivated) {
        Medicine med = remWithMed.medicine;
        Reminder rem = remWithMed.reminder;

        medName = med.getName();
        dailyIntake = med.getDailyIntake();
        reminderTime = rem.getReminderTime();
        imgPath = med.getImagePath();
        this.isActivated = isActivated;
        isPending = TextUtils.equals(rem.getReminderType(), "Pending");
    }

    public String getMedNameText() {
        return String.format(Locale.getDefault(),
                "%s (%d times a day)", medName, dailyIntake);
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public String getReminderTimeStr() {
        return TimeHelper.formatLocalDateTime(reminderTime);
    }

    public String getImgPath() {
        return imgPath;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public boolean isPending() {
        return isPending;
    }

    @BindingAdapter(value = "checkedStatus")
    public static void setCheckedAndActivated(MaterialCardView view, boolean isActivated) {
        view.setChecked(isActivated);
        view.setActivated(isActivated);
    }

    @BindingAdapter(value = "hideIfNotTrue")
    public static void hideIfNotTrue(View view, boolean isPending) {
        view.setVisibility(isPending ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter(value = "android:updater")
    public static void startTextUpdater(TextView view, LocalDateTime localDateTime) {
        view.setText(TimeHelper.calculateLocalDateTimeDiff(localDateTime));
        long delayMillis = (60 - LocalTime.now().getSecond()) * 1000;
        view.postDelayed(new RemainingTextUpdater(view, localDateTime), delayMillis);
    }
}
