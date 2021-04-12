package com.gbsoft.smartpatient.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Timber.d("onReceive has been called after reboot.");
            RescheduleRemindersService.enqueueWork(context);
        }
    }
}
