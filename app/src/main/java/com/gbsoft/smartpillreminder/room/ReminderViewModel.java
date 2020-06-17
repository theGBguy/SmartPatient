package com.gbsoft.smartpillreminder.room;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.gbsoft.smartpillreminder.model.Reminder;

import java.io.File;
import java.util.List;

public class ReminderViewModel extends AndroidViewModel {

    private ReminderRepository reminderRepository;
    private MutableLiveData<String> reminderType;
    private MutableLiveData<Long> id;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        reminderRepository = new ReminderRepository(application);
        reminderType = new MutableLiveData<>();
        id = new MutableLiveData<>();
    }

    public LiveData<List<Reminder>> getAllRemindersByType(String reminderType) {
        this.reminderType.setValue(reminderType);
        return Transformations.switchMap(this.reminderType, new Function<String, LiveData<List<Reminder>>>() {
            @Override
            public LiveData<List<Reminder>> apply(String input) {
                return reminderRepository.getAllRemindersByType(input);
            }
        });
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return reminderRepository.getAllReminders();
    }

    public LiveData<Reminder> getAReminder(long id) {
        this.id.setValue(id);
        return Transformations.switchMap(this.id, new Function<Long, LiveData<Reminder>>() {
            @Override
            public LiveData<Reminder> apply(Long input) {
                return reminderRepository.getAReminder(input);
            }
        });
    }

    public List<Reminder> getAllRemindersByImgPath(String imgPath) {
        return reminderRepository.getAllRemindersByImgPath(imgPath);
    }

    public void deleteAReminder(final Reminder reminder) {
        reminderRepository.deleteAReminder(reminder.getId());
        Runnable deleter = new Runnable() {
            @Override
            public void run() {
                List<Reminder> remindersWithSameImg = getAllRemindersByImgPath(reminder.getImagePath());
                if (remindersWithSameImg.size() == 0) {
                    if (new File(reminder.getImagePath()).delete()) {
                        Log.d("Reminder_View_Model", "Unnecessary image files cleaned up successfully.");
                    }
                }
            }
        };
        new Thread(deleter).start();
    }

    public void insertAReminder(Reminder reminder) {
        reminderRepository.insertAReminder(reminder);
    }

    public void updateAReminder(Reminder reminder) {
        reminderRepository.updateAReminder(reminder);
    }
}
