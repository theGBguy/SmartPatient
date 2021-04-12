package com.gbsoft.smartpatient.ui.reminder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.ActivityReminderBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReminderActivity extends AppCompatActivity {

    private ReminderViewModel viewModel;
    private Animatable2Compat.AnimationCallback animationCallback;
    private AnimatedVectorDrawableCompat drawableCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableFullScreenAndShowWhenLocked();

        ActivityReminderBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);

        viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        viewModel.acquireWakeLock();
        viewModel.createNotificationChannel();

        // start a handler after 2 minutes to set the reminder as missed
        // by finishing the activity
        new Handler().postDelayed(this::finish, 30000);

        if (savedInstanceState == null) {
            viewModel.getReminderFromBundle(getIntent());
        }

        viewModel.playReminderTone();

        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        drawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.reminder_ringing);
        animationCallback = new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                drawableCompat.start();
            }
        };
        if (drawableCompat != null) {
            binding.ivReminderSet.setImageDrawable(drawableCompat);
            drawableCompat.registerAnimationCallback(animationCallback);
            drawableCompat.start();
        }

        viewModel.shouldFinish().observe(this, aBoolean -> {
            if (aBoolean) finish();
        });

    }

    private void enableFullScreenAndShowWhenLocked() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
    }

    @Override
    protected void onDestroy() {
        viewModel.releaseWakeLock();
        viewModel.stopReminderTone();
        viewModel.setReminderAsMissed();
        drawableCompat.unregisterAnimationCallback(animationCallback);
        viewModel = null;
        super.onDestroy();
    }
}
