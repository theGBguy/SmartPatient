package com.gbsoft.smartpillreminder;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.mapbox.mapboxsdk.Mapbox;

import timber.log.Timber;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = sharedPreferences.getBoolean(getString(R.string.key_night_mode), false);
        if (nightMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }
}
