package com.gbsoft.smartpatient.intermediaries.local;

import androidx.room.RoomDatabase;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Medicine.class, Reminder.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocalDao localDao();

    public static final ExecutorService executorService = Executors.newFixedThreadPool(4);
}
