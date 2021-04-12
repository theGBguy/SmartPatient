package com.gbsoft.smartpatient.intermediaries.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;

import java.util.List;

@Dao
public interface LocalDao {

    @Query("SELECT * FROM meds_table WHERE img_path =:imgPath")
    List<Medicine> getAllMedicinesByImgPath(String imgPath);

    @Query("SELECT * FROM meds_table INNER JOIN rems_table ON med_id = medicine_id WHERE reminder_type =:reminderType")
    LiveData<List<ReminderWithMedicine>> getAllReminderWithMedicineByReminderType(String reminderType);

    @Query("SELECT * FROM meds_table INNER JOIN rems_table ON med_id = medicine_id WHERE reminder_type = 'Pending'")
    LiveData<List<ReminderWithMedicine>> getAllPendingRemindersWithMedicineLive();

    @Query("SELECT * FROM meds_table INNER JOIN rems_table ON med_id = medicine_id WHERE reminder_type = 'Pending'")
    List<ReminderWithMedicine> getAllPendingRemindersWithMedicine();

    // related to medicines
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAMedicine(Medicine medicine);

    @Query("DELETE FROM meds_table WHERE med_id =:id")
    void deleteAMedicine(long id);

    @Update
    void updateAMedicine(Medicine medicine);

    @Query("SELECT * FROM meds_table WHERE med_id =:id")
    LiveData<Medicine> getAMedicine(long id);

    @Query("SELECT * FROM meds_table")
    LiveData<List<Medicine>> getAllMedicines();


    @Query("SELECT * FROM rems_table WHERE reminder_type =:reminderType")
    LiveData<List<Reminder>> getAllRemindersByReminderType(String reminderType);

    // related to reminders
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAReminder(Reminder reminder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllReminders(List<Reminder> reminders);

    @Query("DELETE FROM rems_table WHERE reminder_id =:id")
    void deleteAReminder(long id);

    @Update
    void updateAReminder(Reminder reminder);

    @Update
    void updateAllReminders(List<Reminder> reminders);

    @Query("SELECT * FROM rems_table WHERE reminder_id =:id")
    LiveData<Reminder> getAReminder(long id);

    @Query("SELECT * FROM rems_table")
    LiveData<List<Reminder>> getAllReminders();

    @Query("SELECT * FROM rems_table WHERE medicine_id =:medId")
    List<Reminder> getAllRemindersOfAMedicine(long medId);

    @Query("SELECT * FROM rems_table WHERE medicine_id =:medId AND reminder_type =:reminderType")
    List<Reminder> getAllRemindersOfAMedicineByReminderType(long medId, String reminderType);

}
