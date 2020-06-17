package com.gbsoft.smartpillreminder.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("onReceiveSPM", "onReceive has been called after reboot.");
            RescheduleRemindersService.rescheduleReminders(context);
        }
    }
}
