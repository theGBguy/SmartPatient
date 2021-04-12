package com.gbsoft.smartpatient.intermediaries.local;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.preference.PreferenceManager;

import com.gbsoft.smartpatient.R;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class PrefsHelper {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private final Context appContext;

    @Inject
    public PrefsHelper(@ApplicationContext Context context) {
        appContext = context.getApplicationContext();
    }

    public void setUsername(String username) {
        editSharedPreference(KEY_USERNAME, username);
    }

    public void setPassword(String password) {
        editSharedPreference(KEY_PASSWORD, password);
    }

    public String getUsername() {
        return getSharedPreference(KEY_USERNAME);
    }

    public String getPassword() {
        return getSharedPreference(KEY_PASSWORD);
    }

    private void editSharedPreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(appContext)
                .edit()
                .putString(key, value)
                .apply();
    }

    private String getSharedPreference(String key) {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
                .getString(key, "");
    }

    public void setDefaultAlarm() {
        String reminderToneUri = getSharedPreference(appContext.getString(R.string.key_reminder_tone));
        if (reminderToneUri.length() == 0) {
            Uri defaultReminderToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            editSharedPreference(appContext.getString(R.string.key_reminder_tone), defaultReminderToneUri.toString());
        }
    }
}
