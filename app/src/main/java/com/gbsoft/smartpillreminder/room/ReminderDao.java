package com.gbsoft.smartpillreminder.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gbsoft.smartpillreminder.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAReminder(Reminder reminder);

    @Query("SELECT * FROM reminders_table WHERE id=:id")
    LiveData<Reminder> getAReminder(long id);

    @Query("SELECT * FROM reminders_table WHERE image_path=:imgPath")
    List<Reminder> getAllRemindersByImgPath(String imgPath);

    @Query("SELECT * FROM reminders_table WHERE reminder_type =:reminderType")
    LiveData<List<Reminder>> getAllRemindersByType(String reminderType);

    @Query("SELECT * FROM reminders_table WHERE reminder_type = 'Pending'")
    List<Reminder> getAllPendingReminders();

    @Query("SELECT * FROM reminders_table")
    LiveData<List<Reminder>> getAllReminders();

    @Query("DELETE FROM reminders_table WHERE id =:id ")
    void deleteAReminder(long id);

    @Update
    void updateAReminder(Reminder updateMed);

}
