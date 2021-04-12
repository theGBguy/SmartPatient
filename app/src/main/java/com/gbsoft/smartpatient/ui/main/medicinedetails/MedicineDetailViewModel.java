package com.gbsoft.smartpatient.ui.main.medicinedetails;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.utils.TimeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MedicineDetailViewModel extends ViewModel {

    private final MutableLiveData<Medicine> medicine = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> snackMsg = new MutableLiveData<>();

    private RemoteRepo remoteRepo;

    @Inject
    public MedicineDetailViewModel(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
    }

    public void init(Bundle args) {
        if (args == null) return;
        isHp.setValue(args.getBoolean(MedicineDetailFrag.KEY_IS_HP, false));
        medicine.setValue(args.getParcelable(MedicineDetailFrag.KEY_MEDICINE));
    }

    public LiveData<List<Reminder>> reminders = Transformations.switchMap(medicine, input -> {
        if (input == null || remoteRepo == null)
            return new MutableLiveData<>(new ArrayList<>());
        return remoteRepo.getRemindersOfAMedicine(input.getId());
    });

    public void deleteMedicineAndItsReminders() {
        LiveData<Boolean> isDeleted = remoteRepo.deleteMedicineAndItsReminders(medicine.getValue(), isHp.getValue().booleanValue());
        isDeleted.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isDeleted.removeObserver(this);
                snackMsg.setValue(R.string.deletionSuccess_txt);
            }
        });
    }

    public LiveData<String> medicineName = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return input.getName();
    });

    public LiveData<String> medicineType = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return String.format(Locale.getDefault(), "Medicine type : %s", input.getType());
    });

    public LiveData<String> dailyIntake = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return String.format(Locale.getDefault(), "Daily intake : %d", input.getDailyIntake());
    });

    public LiveData<String> expiryDate = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return String.format(Locale.getDefault(), "Expiry date : %s", TimeHelper.formatLocalDate(input.getExpiryDate()));
    });

    public LiveData<String> prescribedBy = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return String.format(Locale.getDefault(), "Prescribed by : %s", input.getPrescribedBy().split("_")[1]);
    });

    public LiveData<String> prescribedTo = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return String.format(Locale.getDefault(), "Prescribed to : %s", input.getPrescribedTo().split("_")[1]);
    });

    public LiveData<String> medicineNotes = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return String.format(Locale.getDefault(), "Medicine notes : %s", input.getMedicineNotes());
    });

    public LiveData<String> imagePath = Transformations.map(medicine, input -> {
        if (input == null) return "";
        return input.getImagePath();
    });

    public LiveData<Boolean> isHp() {
        return isHp;
    }

    public LiveData<Integer> getSnackMsg() {
        return snackMsg;
    }
}