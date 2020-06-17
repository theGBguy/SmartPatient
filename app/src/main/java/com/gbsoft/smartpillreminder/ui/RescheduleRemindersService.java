package com.gbsoft.smartpillreminder.ui;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.room.ReminderRepository;
import com.gbsoft.smartpillreminder.utils.Helper;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RescheduleRemindersService extends IntentService {

    private static final String ACTION_RESCHEDULE_REMINDERS = "com.gbsoft.smartpillreminder.ui.action.RESCHEDULE_REMINDERS";

    private Helper.ReminderHelper reminderHelper;

    public RescheduleRemindersService() {
        super("RescheduleRemindersService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        reminderHelper = new Helper.ReminderHelper(getApplicationContext());
    }

    public static void rescheduleReminders(Context context) {
        Intent intent = new Intent(context, RescheduleRemindersService.class);
        intent.setAction(ACTION_RESCHEDULE_REMINDERS);
        context.startService(intent);
        Log.d("onReceiveSPM", "rescheduleReminders is called.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("onReceiveSPM", "onHandleIntent is called.");
        if ((intent != null) && ACTION_RESCHEDULE_REMINDERS.equals(intent.getAction())) {
            executeRescheduling();
            Log.d("onReceiveSPM", "Rescheduling of reminders has been completed");
        }
    }

    private void executeRescheduling() {
        ReminderRepository reminderRepository = new ReminderRepository(getApplication());
        Log.d("onReceiveSPM", "rescheduling the reminders is ongoing.");
        List<Reminder> reminders = reminderRepository.getAllPendingReminders();
        for (Reminder reminder : reminders) {
            reminderHelper.scheduleReminder(reminder, false);
        }
    }
}
