package com.gbsoft.smartpatient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.ui.reminder.ReminderActivity;

import java.time.ZoneOffset;
import java.util.List;

import timber.log.Timber;

public class ReminderHelper {
    public static final String KEY_IS_ONLINE = "is_online";
    public static final String KEY_REMINDER_WITH_MEDICINE = "key_medicine";

    public static void scheduleReminder(Context context, Medicine medicine, List<Reminder> reminders, boolean isUpdate, boolean isOnline) {
        AlarmManager reminderManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isUpdate)
            cancelAllReminders(context, medicine, reminders, isOnline);

        for (Reminder reminder : reminders) {
            ReminderWithMedicine remWithMed = new ReminderWithMedicine(medicine, reminder);
            PendingIntent intent = getPendingIntent(context, remWithMed, PendingIntent.FLAG_NO_CREATE, true);
            if (intent == null) {
                long time = reminder.getReminderTime().atOffset(ZoneOffset.ofHoursMinutes(5, 45)).toInstant().toEpochMilli();
                reminderManager.setWindow(AlarmManager.RTC_WAKEUP,
                        time,
                        15000,
                        getPendingIntent(context, remWithMed, PendingIntent.FLAG_UPDATE_CURRENT, isOnline));
                Timber.d("A reminder has been scheduled after " + time + " millis");
            } else
                Timber.d("Reminder is already scheduled for this time");
        }
    }

    public static void scheduleReminder(Context context, ReminderWithMedicine remWithMed, boolean isUpdate, boolean isOnline) {
        AlarmManager reminderManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isUpdate)
            cancelAReminder(context, remWithMed, isOnline);

        long time = remWithMed.reminder.getReminderTime().atOffset(ZoneOffset.ofHoursMinutes(5, 45)).toInstant().toEpochMilli();
        reminderManager.setWindow(AlarmManager.RTC_WAKEUP,
                time,
                15000,
                getPendingIntent(context, remWithMed, PendingIntent.FLAG_UPDATE_CURRENT, isOnline));
        Timber.d("A reminder has been scheduled after " + time + " millis");
    }

    public static void cancelAllReminders(Context context, Medicine medicine, List<Reminder> reminders, boolean isOnline) {
        AlarmManager reminderManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (Reminder reminder : reminders) {
            PendingIntent existing = getPendingIntent(context, new ReminderWithMedicine(medicine, reminder), PendingIntent.FLAG_NO_CREATE, isOnline);
            if (existing != null) {
                reminderManager.cancel(existing);
                existing.cancel();
                Timber.d("The reminder has been cancelled successfully!");
            }
        }
    }

    public static void cancelAReminder(Context context, ReminderWithMedicine remWithMed, boolean isOnline) {
        AlarmManager reminderManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent existing = getPendingIntent(context, remWithMed, PendingIntent.FLAG_NO_CREATE, isOnline);
        if (existing != null) {
            reminderManager.cancel(existing);
            existing.cancel();
            Timber.d("The reminder has been cancelled successfully!");
        }
    }

    private static PendingIntent getPendingIntent(Context context, ReminderWithMedicine remWithMed, int flag, boolean isOnline) {
        Intent intent = new Intent(context, ReminderActivity.class);
        //marshalling the remWithMed object because parcelable wouldn't work as intent extra in PendingIntent
        byte[] bytes = ParcelableHelper.marshall(remWithMed);
        intent.putExtra(KEY_REMINDER_WITH_MEDICINE, bytes);
        intent.putExtra(KEY_IS_ONLINE, isOnline);
        return PendingIntent.getActivity(context, (int) remWithMed.reminder.getReminderId(), intent, flag);
    }
}
