package com.gbsoft.smartpillreminder.ui;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.SplashActivityMainBinding;
import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;
import com.gbsoft.smartpillreminder.ui.main.MainFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView ivSplashMain;
    private Handler handler;
    private static OpenMainFragmentRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        String reminderToneUri = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_reminder_tone), "");
        if (reminderToneUri != null && reminderToneUri.length() == 0) {
            Uri defaultReminderToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(getString(R.string.key_reminder_tone), defaultReminderToneUri.toString())
                    .apply();
        }
        if (savedInstanceState == null) {

            com.gbsoft.smartpillreminder.databinding.SplashActivityMainBinding binding = SplashActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            handler = new Handler();

            ReminderViewModel reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
            reminderViewModel.getAllReminders().observe(this, new Observer<List<Reminder>>() {
                @Override
                public void onChanged(List<Reminder> reminders) {
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
                }
            });

            ivSplashMain = binding.ivSplashMain;
            AnimatedVectorDrawableCompat drawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.splash_anim);
            ivSplashMain.setImageDrawable(drawableCompat);
            if (drawableCompat != null) drawableCompat.start();
            openMainFragment(650);
        } else
            openMainFragment(0);
    }

    private void openMainFragment(final long delayInMillis) {
        runnable = new OpenMainFragmentRunnable(this, delayInMillis);
        handler.postDelayed(runnable, delayInMillis);
    }

    private static class OpenMainFragmentRunnable implements Runnable {
        private WeakReference<MainActivity> mainActivityWeakReference;
        private long delays;

        OpenMainFragmentRunnable(MainActivity mainActivity, long delays) {
            this.mainActivityWeakReference = new WeakReference<>(mainActivity);
            this.delays = delays;
        }

        @Override
        public void run() {
            final String FRAGMENT_TAG = "main_fragment";
            FragmentManager fragmentManager = mainActivityWeakReference.get().getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, new MainFragment(), FRAGMENT_TAG)
                        .commit();
                if (delays != 0 && mainActivityWeakReference.get() != null)
                    mainActivityWeakReference.get().ivSplashMain.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        runnable = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ivSplashMain = null;
    }
}