package com.gbsoft.smartpillreminder.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.gbsoft.smartpillreminder.model.Reminder;

import java.util.List;

public class ReminderRepository {
    private ReminderDao reminderDao;

    public ReminderRepository(Application application) {
        ReminderDatabase rd = ReminderDatabase.getINSTANCE(application);
        reminderDao = rd.reminderDao();
    }

    LiveData<List<Reminder>> getAllRemindersByType(String reminderType) {
        return reminderDao.getAllRemindersByType(reminderType);
    }

    LiveData<List<Reminder>> getAllReminders() {
        return reminderDao.getAllReminders();
    }

    public List<Reminder> getAllPendingReminders() {
        return reminderDao.getAllPendingReminders();
    }

    LiveData<Reminder> getAReminder(long id) {
        return reminderDao.getAReminder(id);
    }

    List<Reminder> getAllRemindersByImgPath(String imgPath) {
        return reminderDao.getAllRemindersByImgPath(imgPath);
    }

    void insertAReminder(Reminder Reminder) {
        new InsertOrUpdateAReminder(true, reminderDao).execute(Reminder);
    }

    void updateAReminder(Reminder Reminder) {
        new InsertOrUpdateAReminder(false, reminderDao).execute(Reminder);
    }

    void deleteAReminder(long id) {
        new DeleteAReminder(reminderDao).execute(id);
    }

    static class InsertOrUpdateAReminder extends AsyncTask<Reminder, Void, Void> {
        private ReminderDao reminderDao;
        private boolean insert;

        InsertOrUpdateAReminder(boolean insert, ReminderDao reminderDao) {
            this.insert = insert;
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Reminder... medicines) {
            if (insert)
                reminderDao.insertAReminder(medicines[0]);
            else
                reminderDao.updateAReminder(medicines[0]);
            return null;
        }
    }

    static class DeleteAReminder extends AsyncTask<Long, Void, Void> {
        private ReminderDao reminderDao;

        DeleteAReminder(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Long... ids) {
            reminderDao.deleteAReminder(ids[0]);
            return null;
        }
    }


}
