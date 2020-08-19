package com.gbsoft.smartpillreminder.ui;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.ActivityMainNormalBinding;
import com.gbsoft.smartpillreminder.databinding.ActivityMainSplashBinding;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainNormalBinding normalBinding;
    private NavController navController;
    private AppBarConfiguration appBarConfig;

    private AnimatedVectorDrawableCompat drawableCompat;
    private Animatable2Compat.AnimationCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        normalBinding = ActivityMainNormalBinding.inflate(getLayoutInflater());
        if (savedInstanceState == null) {
            ActivityMainSplashBinding splashBinding = ActivityMainSplashBinding.inflate(getLayoutInflater());
            setContentView(splashBinding.getRoot());

            drawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.splash_anim);
            splashBinding.ivSplashMain.setImageDrawable(drawableCompat);

            callback = new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    setupNormalView();
                }
            };

            drawableCompat.registerAnimationCallback(callback);
            drawableCompat.start();

            ReminderViewModel reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
            reminderViewModel.getAllReminders().observe(this, reminders -> {
                ComponentName bootCompletedReceiver = new ComponentName(MainActivity.this, BootCompletedReceiver.class);
                if (reminders.size() > 0) {
                    getPackageManager().setComponentEnabledSetting(bootCompletedReceiver,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    getPackageManager().setComponentEnabledSetting(bootCompletedReceiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
            });

            setDefaultAlarm();

        } else {
            setupNormalView();
        }
    }

    private void setupNormalView() {
        setContentView(normalBinding.getRoot());

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (host != null) {
            navController = host.getNavController();
            NavigationUI.setupWithNavController(normalBinding.navigationView, navController);
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.navigation_settings
                        || destination.getId() == R.id.addOrUpdateReminderFragment) {
                    normalBinding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                } else {
                    normalBinding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            });
        }
    }

    public AppBarConfiguration getAppBarConfig() {
        if (appBarConfig == null) {
            appBarConfig = new AppBarConfiguration.Builder(R.id.navigation_home,
                    R.id.navigation_nearby)
                    .setOpenableLayout(normalBinding.mainDrawerLayout)
                    .build();
        }
        return appBarConfig;
    }


    private void setDefaultAlarm() {
        String reminderToneUri = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_reminder_tone), "");
        if (reminderToneUri != null && reminderToneUri.length() == 0) {
            Uri defaultReminderToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(getString(R.string.key_reminder_tone), defaultReminderToneUri.toString())
                    .apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (normalBinding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            normalBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (drawableCompat != null) drawableCompat.unregisterAnimationCallback(callback);
    }
}