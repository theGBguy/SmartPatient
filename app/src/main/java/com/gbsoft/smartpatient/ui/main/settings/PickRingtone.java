package com.gbsoft.smartpatient.ui.main.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PickRingtone extends ActivityResultContract<String, Uri> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String existingValue) {
        Intent reminderToneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);

        if (existingValue != null && existingValue.length() > 0)
            reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));

        return reminderToneIntent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode != Activity.RESULT_OK) return null;
        if (intent == null) return null;
        return intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
    }
}
