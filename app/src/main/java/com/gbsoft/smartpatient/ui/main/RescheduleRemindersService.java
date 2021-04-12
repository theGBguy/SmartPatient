package com.gbsoft.smartpatient.ui.main;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.intermediaries.local.LocalRepo;
import com.gbsoft.smartpatient.utils.ReminderHelper;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

/**
 * An {@link JobIntentService} subclass for handling asynchronous task requests in
 * a service or work manager.
 */
@AndroidEntryPoint
public class RescheduleRemindersService extends JobIntentService {
    public static final String ACTION_RESCHEDULE_REMINDERS = "com.gbsoft.smartpatient.ui.action.RESCHEDULE_REMINDERS";

    @Inject
    LocalRepo localRepo;

    static void enqueueWork(Context context) {
        Intent intent = new Intent(context, RescheduleRemindersService.class);
        intent.setAction(ACTION_RESCHEDULE_REMINDERS);
        enqueueWork(context, RescheduleRemindersService.class, 100, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (ACTION_RESCHEDULE_REMINDERS.equals(intent.getAction())) {
            executeRescheduling();
            Timber.d("Rescheduling of reminders has been completed");
        }
    }

    private void executeRescheduling() {
        Timber.d("rescheduling the medicine's reminder is ongoing.");
        List<ReminderWithMedicine> pendingMedicines = localRepo.getAllPendingReminderWithMedicines();
        for (ReminderWithMedicine remWithMed : pendingMedicines) {
            ReminderHelper.scheduleReminder(getApplicationContext(), remWithMed, false, false);
        }
    }
}
