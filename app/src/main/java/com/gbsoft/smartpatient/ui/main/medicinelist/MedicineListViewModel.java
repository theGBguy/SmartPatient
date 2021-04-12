package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.intermediaries.local.LocalRepo;
import com.gbsoft.smartpatient.utils.ReminderHelper;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

@HiltViewModel
public class MedicineListViewModel extends AndroidViewModel {
    private final LocalRepo localRepo;
    private final MutableLiveData<String> reminderType = new MutableLiveData<>();
    private LiveData<List<ReminderWithMedicine>> getAllMedicinesByReminderType;

    @Inject
    public MedicineListViewModel(@NonNull Application application, LocalRepo localRepo) {
        super(application);
        this.localRepo = localRepo;
    }

    public void setReminderType(@Nullable Bundle args) {
        if (args == null) return;
        int position = args.getInt(MedicineListFrag.POSITION_KEY, -1);
        setReminderType(getAppContext().getResources().getStringArray(R.array.reminder_type_array_res)[position]);
    }

    public void setReminderType(String reminderType) {
        this.reminderType.setValue(reminderType);
    }

    public LiveData<List<ReminderWithMedicine>> getAllMedicinesByReminderType() {
        if (getAllMedicinesByReminderType == null) {
            getAllMedicinesByReminderType = Transformations
                    .switchMap(reminderType, localRepo::getAllMedicinesByReminderType);
        }
        return getAllMedicinesByReminderType;
    }

    public void deleteAReminder(final ReminderWithMedicine remWithMed) {
        localRepo.deleteAReminder(remWithMed.reminder.getReminderId());
        ReminderHelper.cancelAReminder(getAppContext(), remWithMed, false);
        List<Reminder> reminders = localRepo.getAllRemindersOfAMedicine(remWithMed.medicine.getId());
        if (reminders.size() == 0) {
            localRepo.deleteAMedicine(remWithMed.medicine.getId());
            List<Medicine> medicines = localRepo.getAllMedicinesByImgPath(remWithMed.medicine.getImagePath());
            if (medicines.size() == 0) {
                if (new File(remWithMed.medicine.getImagePath()).delete()) {
                    Timber.d("Unnecessary image files cleaned up successfully.");
                }
            }
        }
    }

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}
