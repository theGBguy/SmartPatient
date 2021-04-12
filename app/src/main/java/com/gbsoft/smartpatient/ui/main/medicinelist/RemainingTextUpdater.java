package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.widget.TextView;

import com.gbsoft.smartpatient.utils.TimeHelper;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;

public class RemainingTextUpdater implements Runnable {
    private final LocalDateTime reminderTime;
    private final WeakReference<TextView> textView;

    RemainingTextUpdater(TextView textView, LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
        this.textView = new WeakReference<>(textView);
    }

    @Override
    public void run() {
        TextView tv = textView.get();
        if (tv == null) return;
        tv.setText(TimeHelper.calculateLocalDateTimeDiff(reminderTime));
        tv.postDelayed(this, 60 * 1000);
    }
}
