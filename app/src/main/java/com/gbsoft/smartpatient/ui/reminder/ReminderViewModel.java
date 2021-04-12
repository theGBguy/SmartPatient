package com.gbsoft.smartpatient.ui.reminder;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.intermediaries.local.LocalRepo;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.utils.ParcelableHelper;
import com.gbsoft.smartpatient.utils.ReminderHelper;
import com.gbsoft.smartpatient.utils.TimeHelper;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReminderViewModel extends AndroidViewModel {
    @SuppressLint("StaticFieldLeak")
    private Context appContext;

    private final MutableLiveData<Boolean> shouldFinish = new MutableLiveData<>(false);
    private final MutableLiveData<String> medName = new MutableLiveData<>();
    private final MutableLiveData<String> imgPath = new MutableLiveData<>();

    private final LocalRepo localRepo;
    private final RemoteRepo remoteRepo;
    private PowerManager.WakeLock wakeLock;
    private ReminderWithMedicine remWithMed;

    private Ringtone reminderTone;
    private boolean isMissed = true;
    private boolean isOnline;

    @Inject
    public ReminderViewModel(@NonNull Application application, LocalRepo localRepo, RemoteRepo remoteRepo) {
        super(application);
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
        appContext = application.getApplicationContext();
    }

    MutableLiveData<Boolean> shouldFinish() {
        return shouldFinish;
    }

    void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "smartpatient:reminderactivity");
        wakeLock.acquire(60 * 1000L /*1 minute*/);
    }

    void releaseWakeLock() {
        wakeLock.release();
    }

    void playReminderTone() {
        String reminderToneUriStr = PreferenceManager.getDefaultSharedPreferences(appContext).getString(appContext.getString(R.string.key_reminder_tone), "");
        reminderTone = RingtoneManager.getRingtone(appContext, Uri.parse(reminderToneUriStr));
        reminderTone.play();
    }

    void stopReminderTone() {
        reminderTone.stop();
    }

    void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (appContext == null)
                appContext = getApplication().getApplicationContext();
            NotificationChannel channel = new NotificationChannel(appContext.getString(R.string.notif_channel_id),
                    appContext.getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(appContext.getString(R.string.notif_channel_desc));
            channel.setBypassDnd(true);
            channel.enableVibration(true);
            channel.enableLights(true);
            ((NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }

    void showNotification() {
        if (appContext == null)
            appContext = getApplication().getApplicationContext();
        String reminderTime = TimeHelper.formatLocalDateTime(remWithMed.reminder.getReminderTime());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, appContext.getString(R.string.notif_channel_id));
        builder.setContentTitle(appContext.getString(R.string.notif_missed_title))
                .setContentText(appContext.getString(R.string.notif_content, reminderTime))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(appContext).notify(2468, builder.build());
    }

    public void onBtnStopReminderClick() {
        remWithMed.reminder.setReminderType("Completed");
        if (isOnline)
            remoteRepo.updateAReminder(remWithMed.reminder);
        else
            localRepo.updateAReminder(remWithMed.reminder);
        isMissed = false;
        shouldFinish.setValue(true);
    }

    void setReminderAsMissed() {
        if (isMissed && remWithMed != null) {
            remWithMed.reminder.setReminderType("Missed");
            if (isOnline)
                remoteRepo.updateAReminder(remWithMed.reminder);
            else
                localRepo.updateAReminder(remWithMed.reminder);
            showNotification();
        }
    }

    void getReminderFromBundle(Intent intent) {
        if (intent != null) {
            byte[] bytes = intent.getByteArrayExtra(ReminderHelper.KEY_REMINDER_WITH_MEDICINE);
            isOnline = intent.getBooleanExtra(ReminderHelper.KEY_IS_ONLINE, false);
            remWithMed = ParcelableHelper.unmarshall(bytes, ReminderWithMedicine.CREATOR);
            medName.setValue(remWithMed.medicine.getName());
            imgPath.setValue(remWithMed.medicine.getImagePath());
        }
    }

    public MutableLiveData<String> getImgPath() {
        return imgPath;
    }

    public MutableLiveData<String> getMedName() {
        return medName;
    }

    @Override
    protected void onCleared() {
        appContext = null;
        super.onCleared();
    }
}
