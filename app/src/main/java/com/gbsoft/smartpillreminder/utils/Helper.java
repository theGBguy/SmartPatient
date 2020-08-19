package com.gbsoft.smartpillreminder.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.ui.ReminderActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.Date;

public class Helper {
    private static byte[] marshall(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    private static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    public static class TimeHelper {
        String calculateTimeDiff(Date futureDate) {
            StringBuilder timeDiffStr = new StringBuilder();
            int hr, min, sec;
            if (new Date().before(futureDate)) {
                Calendar then = Calendar.getInstance();
                then.setTime(futureDate);
                Calendar now = Calendar.getInstance();
                hr = then.get(Calendar.HOUR) - now.get(Calendar.HOUR);
                min = then.get(Calendar.MINUTE) - now.get(Calendar.MINUTE);
                sec = then.get(Calendar.SECOND) - now.get(Calendar.SECOND);
                if (hr == 1)
                    timeDiffStr.append(hr).append(" hr ");
                else
                    timeDiffStr.append(hr).append(" hrs ");
                if (min == 1)
                    timeDiffStr.append(min).append(" min ");
                else
                    timeDiffStr.append(min).append(" mins ");
                if (sec == 1)
                    timeDiffStr.append(sec).append(" sec ");
                else
                    timeDiffStr.append(sec).append(" secs ");
                timeDiffStr.append(" remaining");
                return timeDiffStr.toString();
            } else
                return "";
        }

        // input format : 18:5  output format : 5 mins remaining

        public String calculateTimeDiff(final String reminderTime) {
            int[] remainingTime = getRemHrMin(reminderTime);
            int remainingHr = remainingTime[0], remainingMin = remainingTime[1];
            if (remainingHr == 0) {
                if (remainingMin == 0)
                    return "about to go off";
                else
                    return remainingMin + " min" + ((remainingMin == 1) ? " " : "s ") + "remaining";
            } else {
                if (remainingMin == 0)
                    return remainingHr + " hr" + ((remainingHr == 1) ? " " : "s ") + "remaining";
                else
                    return remainingHr + " hr" + ((remainingHr == 1) ? " " : "s ") +
                            remainingMin + " min" + ((remainingMin == 1) ? " " : "s ") + "remaining";
            }
        }

        public int[] getRemHrMin(String reminderTime) {
            String[] splittedTimeStr = reminderTime.split(":");
            int hr, min, hrNow, minNow, remainingHr, remainingMin;

            hr = Integer.parseInt(splittedTimeStr[0]);
            min = Integer.parseInt(splittedTimeStr[1]);

            Calendar now = Calendar.getInstance();
            hrNow = now.get(Calendar.HOUR_OF_DAY);
            minNow = now.get(Calendar.MINUTE);

            if (min < minNow) {
                min += 60;
                hr -= 1;
            }
            if (hr < hrNow)
                hr += 24;

            remainingHr = hr - hrNow;
            remainingMin = min - minNow;
            return new int[]{remainingHr, remainingMin};
        }

        // input = 15:50   output = 3:50 PM
        public String formatTime(String reminderTime) {
            String[] splittedAlarmTime = reminderTime.split(":");
            int hr, min;
            hr = Integer.parseInt(splittedAlarmTime[0]);
            String minStr = splittedAlarmTime[1];
            min = Integer.parseInt(minStr);
            if (hr < 12) {
                if (hr == 0)
                    hr = 12;
                return hr + ":" + (minStr.length() == 1 ? "0" : "") + min + " AM";
            } else if (hr < 24) {
                hr -= 12;
                if (hr == 0)
                    hr = 12;
                return hr + ":" + (minStr.length() == 1 ? "0" : "") + min + " PM";
            } else {
                throw new NumberFormatException("The hour value cannot be 24 or greater");
            }

        }

        long getRemainingTimeInLongMillis(String reminderTime) {
            int[] remainingTime = getRemHrMin(reminderTime);
            int hr = remainingTime[0], min = remainingTime[1];
            //format of the remaining time : 4 hrs 2 mins remaining
            System.out.println(hr + " " + min);
            return hr * AlarmManager.INTERVAL_HOUR + min * 60000;
        }

        //input 23:4 output 5:4
        public String addSixHours(String reminderTime) {
            String[] splittedReminderTime = reminderTime.split(":");
            int hr = Integer.parseInt(splittedReminderTime[0]);
            hr += 6;
            if (hr >= 24) hr -= 24;
            return hr + ":" + splittedReminderTime[1];
        }
    }

    public static class ReminderHelper {
        public static final String KEY_REMINDER = "key_reminder";
        private static final String LOG_TAG = "Reminder_Helper";
        private AlarmManager reminderManager;
        private Context context;
        private TimeHelper timeHelper;

        public ReminderHelper(Context context) {
            this.context = context;
            timeHelper = new TimeHelper();
            reminderManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        public void scheduleReminder(Reminder Reminder, boolean isUpdate) {
            if (isUpdate) {
                cancelReminder(Reminder);
                Log.d(LOG_TAG, "The existing reminder has been rescheduled successfully!");
            } else
                Log.d(LOG_TAG, "A new reminder has been scheduled successfully!");
            reminderManager.setWindow(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + timeHelper.getRemainingTimeInLongMillis(Reminder.getReminderTime()),
                    15000,
                    getPendingIntent(Reminder, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        public void cancelReminder(Reminder Reminder) {
            PendingIntent existing = getPendingIntent(Reminder, PendingIntent.FLAG_NO_CREATE);
            if (existing != null) {
                reminderManager.cancel(existing);
                existing.cancel();
                Log.d(LOG_TAG, "The reminder has been cancelled successfully!");
            }
        }

        private PendingIntent getPendingIntent(Reminder Reminder, int flag) {
            Intent intent = new Intent(context, ReminderActivity.class);
            //marshalling the Reminder object because parcelable wouldn't work as intent extra in PendingIntent
            byte[] bytes = marshall(Reminder);
            intent.putExtra(KEY_REMINDER, bytes);
            Log.d("pi_request_code", String.valueOf((int) Reminder.getId()));
            return PendingIntent.getActivity(context, (int) Reminder.getId(), intent, flag);
        }

    }

    public static class ImageHelper {
        public BitmapFactory.Options getSuitableOptions(String imgPath, int ivWidth, int ivHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;
            if (height > ivHeight || width > ivWidth) {
                int halfHt = height / 2;
                int halfWt = width / 2;
                while (((halfHt / inSampleSize) >= ivHeight) && (halfWt / inSampleSize) >= ivWidth) {
                    inSampleSize *= 2;
                }
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            return options;
        }

        public Drawable getDrawableFromPath(Context context, String imgPath, int width, int height) {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(imgPath, getSuitableOptions(imgPath, width, height)));
        }
    }

    public static class NotificationHelper {
        private static void createNotificationChannel(Context context) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(context.getString(R.string.notif_channel_id),
                        context.getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(context.getString(R.string.notif_channel_desc));
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            }
        }


        public static void showNotification(Context context, String reminderTime) {
            createNotificationChannel(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notif_channel_id));
            builder.setContentTitle(context.getString(R.string.notif_missed_title))
                    .setContentText(context.getString(R.string.notif_content, reminderTime))
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat.from(context).notify(2468, builder.build());
        }
    }
}
