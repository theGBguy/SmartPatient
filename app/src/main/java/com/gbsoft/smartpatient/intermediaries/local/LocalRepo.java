package com.gbsoft.smartpatient.intermediaries.local;

import androidx.lifecycle.LiveData;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

public class LocalRepo {
    private final LocalDao localDao;
    private final PrefsHelper prefsHelper;

    @Inject
    public LocalRepo(LocalDao localDao, PrefsHelper prefsHelper) {
        this.localDao = localDao;
        this.prefsHelper = prefsHelper;
    }

    public LiveData<List<ReminderWithMedicine>> getAllMedicinesByReminderType(String reminderType) {
        return localDao.getAllReminderWithMedicineByReminderType(reminderType);
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return localDao.getAllMedicines();
    }

    public List<ReminderWithMedicine> getAllPendingReminderWithMedicines() {
        try {
            Future<List<ReminderWithMedicine>> future = AppDatabase.executorService.submit(localDao::getAllPendingRemindersWithMedicine);
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<Medicine> getAMedicine(long id) {
        return localDao.getAMedicine(id);
    }

    public List<Medicine> getAllMedicinesByImgPath(String imgPath) {
        try {
            Future<List<Medicine>> future = AppDatabase.executorService.submit(() -> localDao.getAllMedicinesByImgPath(imgPath));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Reminder> getAllRemindersOfAMedicine(long medId) {
        try {
            Future<List<Reminder>> future = AppDatabase.executorService.submit(() -> localDao.getAllRemindersOfAMedicine(medId));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertAMedicine(Medicine medicine) {
        AppDatabase.executorService.execute(() -> localDao.insertAMedicine(medicine));
    }

    public void updateAMedicine(Medicine medicine) {
        AppDatabase.executorService.execute(() -> localDao.updateAMedicine(medicine));
    }

    public void deleteAMedicine(long id) {
        AppDatabase.executorService.execute(() -> localDao.deleteAMedicine(id));
    }

    public void insertAReminder(Reminder reminder) {
        AppDatabase.executorService.execute(() -> localDao.insertAReminder(reminder));
    }

    public void insertAllReminders(List<Reminder> reminders) {
        AppDatabase.executorService.execute(() -> localDao.insertAllReminders(reminders));
    }

    public LiveData<Reminder> getAReminder(long id) {
        return localDao.getAReminder(id);
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return localDao.getAllReminders();
    }

    public List<Reminder> getAllRemindersOfAMedicineByReminderType(long medId, String reminderType) {
        try {
            Future<List<Reminder>> future = AppDatabase.executorService.submit(() -> localDao
                    .getAllRemindersOfAMedicineByReminderType(medId, reminderType));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateAReminder(Reminder reminder) {
        AppDatabase.executorService.execute(() -> localDao.updateAReminder(reminder));
    }

    public void updateAllReminders(List<Reminder> reminders) {
        AppDatabase.executorService.execute(() -> localDao.updateAllReminders(reminders));
    }

    public void deleteAReminder(long id) {
        AppDatabase.executorService.execute(() -> localDao.deleteAReminder(id));
    }

    public void setUsername(String username) {
        prefsHelper.setUsername(username);
    }

    public void setPassword(String password) {
        prefsHelper.setPassword(password);
    }

    public void setDefaultAlarm() {
        prefsHelper.setDefaultAlarm();
    }
}
