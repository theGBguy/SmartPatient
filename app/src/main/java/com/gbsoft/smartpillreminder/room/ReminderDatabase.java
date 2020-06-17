package com.gbsoft.smartpillreminder.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gbsoft.smartpillreminder.model.Reminder;

@Database(entities = Reminder.class, version = 1, exportSchema = false)
abstract class ReminderDatabase extends RoomDatabase {

    abstract ReminderDao reminderDao();

    private static volatile ReminderDatabase INSTANCE;

    static ReminderDatabase getINSTANCE(Context context) {
        if (INSTANCE == null) {
            synchronized (ReminderDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ReminderDatabase.class, "reminders_db")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }
}
