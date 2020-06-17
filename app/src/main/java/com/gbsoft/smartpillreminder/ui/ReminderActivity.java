package com.gbsoft.smartpillreminder.ui;

import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.ActivityReminderBinding;
import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;
import com.gbsoft.smartpillreminder.utils.Helper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener {

    private PowerManager.WakeLock wakeLock;
    private Reminder reminder;
    private ReminderViewModel reminderViewModel;
    private boolean isMissed = true;
    private Helper.ReminderHelper reminderHelper;

    private Ringtone reminderTone;

    private Animatable2Compat.AnimationCallback animationCallback;
    private AnimatedVectorDrawableCompat drawableCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        } else {
            setShowWhenLocked(true);
        }
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "smartpillreminder:reminderactivity");
        wakeLock.acquire(60 * 1000L /*1 minute*/);

        ActivityReminderBinding binding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        reminderHelper = new Helper.ReminderHelper(this);

        String reminderToneUriStr = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.key_reminder_tone), "");
        reminderTone = RingtoneManager.getRingtone(this, Uri.parse(reminderToneUriStr));
        reminderTone.play();

        Bundle received = getIntent().getExtras();
        if (received != null) {
            byte[] bytes = received.getByteArray(Helper.ReminderHelper.KEY_REMINDER);
            reminder = Helper.unmarshall(bytes, Reminder.CREATOR);
            if (reminder != null) {
                ShapeableImageView ivMedImg = binding.ivMedImg;
                Drawable imgReminder = new Helper.ImageHelper().getDrawableFromPath(this, reminder.getImagePath(), 300, 300);
                ivMedImg.setImageDrawable(imgReminder);
                TextView tvMedName = binding.tvMedicineName;
                tvMedName.setText(reminder.getMedicineName());
            }
        }

        Chip btnRemind6HrsLater, btnRemindCustomTimeLater;
        btnRemind6HrsLater = binding.btnSetReminderSixHoursLater;
        btnRemindCustomTimeLater = binding.btnSetReminderCustomTimeLater;
        MaterialButton btnStopReminder = binding.btnStopReminder;
        btnRemind6HrsLater.setOnClickListener(this);
        btnRemindCustomTimeLater.setOnClickListener(this);
        btnStopReminder.setOnClickListener(this);

        animationCallback = new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                drawableCompat.start();
            }
        };

        ImageView ivReminderSet = binding.ivReminderSet;
        drawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.reminder_ringing);
        if (drawableCompat != null) {
            drawableCompat.registerAnimationCallback(animationCallback);
        }
        ivReminderSet.setImageDrawable(drawableCompat);
        drawableCompat.start();

    }

    @Override
    protected void onDestroy() {
        wakeLock.release();
        reminderTone.stop();
        drawableCompat.unregisterAnimationCallback(animationCallback);
        if (isMissed && reminder != null) {
            reminder.setReminderType("Missed");
            reminderViewModel.updateAReminder(reminder);
            Helper.NotificationHelper.showNotification(this, new Helper.TimeHelper().formatTime(reminder.getReminderTime()));
        }
        reminderViewModel = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (reminder != null) {
            switch (v.getId()) {
                case R.id.btnSetReminderSixHoursLater:
                    Reminder newer = new Reminder(reminder);
                    newer.setId(System.currentTimeMillis());
                    newer.setReminderType("Pending");
                    newer.setReminderTime(new Helper.TimeHelper().addSixHours(reminder.getReminderTime()));
                    reminderViewModel.insertAReminder(newer);
                    reminderHelper.scheduleReminder(newer, false);
                    reminder.setReminderType("Completed");
                    reminderViewModel.updateAReminder(reminder);
                    isMissed = false;
                    finish();
                    break;
                case R.id.btnSetReminderCustomTimeLater:
                    TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Reminder newer = new Reminder(reminder);
                            newer.setId(System.currentTimeMillis());
                            newer.setReminderType("Pending");
                            newer.setReminderTime(hourOfDay + ":" + minute);
                            reminderViewModel.insertAReminder(newer);
                            reminderHelper.scheduleReminder(newer, false);
                            reminder.setReminderType("Completed");
                            reminderViewModel.updateAReminder(reminder);
                            isMissed = false;
                            finish();
                        }
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                            , Calendar.getInstance().get(Calendar.MINUTE)
                            , false);
                    dialog.show();
                    break;
                case R.id.btnStopReminder:
                    reminder.setReminderType("Completed");
                    reminderViewModel.updateAReminder(reminder);
                    isMissed = false;
                    finish();
                    break;
            }
        }
    }
}
