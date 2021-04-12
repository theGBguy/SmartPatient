package com.gbsoft.smartpatient.di;

import android.content.Context;

import androidx.room.Room;

import com.gbsoft.smartpatient.intermediaries.local.AppDatabase;
import com.gbsoft.smartpatient.intermediaries.local.LocalDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    public LocalDao provideLocalDao(AppDatabase db) {
        return db.localDao();
    }

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context appContext) {
        return Room.databaseBuilder(appContext.getApplicationContext(), AppDatabase.class, "smart_patient_db")
                .fallbackToDestructiveMigration()
                .build();
    }
}
