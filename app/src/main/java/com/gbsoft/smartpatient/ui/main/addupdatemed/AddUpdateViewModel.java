package com.gbsoft.smartpatient.ui.main.addupdatemed;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.local.LocalRepo;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.utils.Event;
import com.gbsoft.smartpatient.utils.ReminderHelper;
import com.gbsoft.smartpatient.utils.TimeHelper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class AddUpdateViewModel extends AndroidViewModel {
    // id, name, type, dailyIntake, imagePath, expiryDate, prescribedBy, {id, reminderTime, reminderType, assignedBy}, medicineNotes
    // medicine related variables
    private long id = -1;
    public MutableLiveData<String> medName = new MutableLiveData<>();
    private final MutableLiveData<String> medType = new MutableLiveData<>();
    public MutableLiveData<String> dailyIntake = new MutableLiveData<>();
    private final MutableLiveData<String> imagePath = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> expiryDate = new MutableLiveData<>();
    public MutableLiveData<String> prescribedBy = new MutableLiveData<>();
    public MutableLiveData<String> medicineNotes = new MutableLiveData<>();

    // reminder related variables
    private final MutableLiveData<List<Reminder>> reminders = new MutableLiveData<>();

    private boolean isUpdate = false;
    private boolean isOnline = false;
    private String prescribedTo;
    private String prescribedByOnline;
    private final LocalRepo localRepo;
    private final RemoteRepo remoteRepo;

    // events
    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();
    private final MutableLiveData<Event<Uri>> addImgEvent = new MutableLiveData<>();

    // others
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> subtitle = new MutableLiveData<>();
    private final MutableLiveData<String> btnAddUpdateTxt = new MutableLiveData<>();

    @Inject
    public AddUpdateViewModel(@NonNull Application application, LocalRepo localRepo, RemoteRepo remoteRepo) {
        super(application);
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
    }

    public void init(Bundle args, User currentUser) {
        isOnline = args.getBoolean(AddUpdateMedicineFrag.KEY_IS_ONLINE, false);
        ReminderWithMedicine remWithMed = args.getParcelable(AddUpdateMedicineFrag.KEY_MEDICINE_ID_TO_BE_UPDATED);

        if (isOnline || remWithMed == null) {
            title.setValue(getAppContext().getString(R.string.fragment_add_medicine_label));
            id = System.currentTimeMillis();
            reminders.setValue(new ArrayList<>());
            expiryDate.setValue(null);
            imagePath.setValue(null);
            btnAddUpdateTxt.setValue(getAppContext().getString(R.string.btnAdd_text));
            if (isOnline) {
                prescribedTo = args.getString(AddUpdateMedicineFrag.KEY_PATIENT_UID) + "_"
                        + args.getString(AddUpdateMedicineFrag.KEY_PATIENT_NAME);
                prescribedByOnline = currentUser.getId() + "_" + currentUser.getName();
            }
            return;
        }

        isUpdate = true;
        title.setValue(getAppContext().getString(R.string.fragment_update_medicine_label));
        subtitle.setValue("\"" + remWithMed.medicine.getName() + "\"");
        btnAddUpdateTxt.setValue(getAppContext().getString(R.string.btnUpdate_text));

        id = remWithMed.medicine.getId();
        medName.setValue(remWithMed.medicine.getName());
        medType.setValue(remWithMed.medicine.getType());
        dailyIntake.setValue(String.valueOf(remWithMed.medicine.getDailyIntake()));
        imagePath.setValue(remWithMed.medicine.getImagePath());
        expiryDate.setValue(remWithMed.medicine.getExpiryDate());
        prescribedBy.setValue(remWithMed.medicine.getPrescribedBy());
        medicineNotes.setValue(remWithMed.medicine.getMedicineNotes());

        reminders.setValue(new ArrayList<>(Collections.singletonList(remWithMed.reminder)));
    }


    // getters and setters only
    public LiveData<String> getTitle() {
        return title;
    }

    public LiveData<String> getSubtitle() {
        return subtitle;
    }

    public LiveData<Integer> getSpinnerSelectionPos() {
        return Transformations.map(medType, input -> {
            List<String> list = Arrays.asList(getAppContext().getResources().getStringArray(R.array.spinner_med_type_array_res));
            return list.indexOf(input);
        });
    }

    public void setMedType(int pos) {
        if (pos == 0) {
            snackMsg.setValue(new Event<>(R.string.spinnerMedTypeNoSelection_text));
            return;
        }
        List<String> list = Arrays.asList(getAppContext().getResources().getStringArray(R.array.spinner_med_type_array_res));
        medType.setValue(list.get(pos));
    }

    public LiveData<String> getReminderTime() {
        return Transformations.map(reminders, input -> {
            if (input.size() == 0)
                return getAppContext().getString(R.string.btnSetReminderTime_text);
            if (input.size() == 1)
                return TimeHelper.formatLocalDateTime(input.get(0).getReminderTime());
            return "Reminder set for multiple times";
        });
    }

    public LiveData<String> getImagePath() {
        return Transformations.map(imagePath, input -> {
            if (input == null || TextUtils.isEmpty(input))
                return getAppContext().getString(R.string.btnAddMedImg_text);
            return new File(input).getName();
        });
    }

    public void startAddImg() {
        addImgEvent.setValue(new Event<>(createImageFile(medName.getValue())));
    }

    public LiveData<Event<Uri>> getAddImgEvent() {
        return addImgEvent;
    }

    public LiveData<String> getExpiryDate() {
        return Transformations.map(expiryDate, input -> {
            if (input == null)
                return getAppContext().getString(R.string.btnSetExpiryDate_text);
            return TimeHelper.formatLocalDate(input);
        });
    }

    public LocalDate getExpiryDateOrig() {
        return expiryDate.getValue();
    }

    public void setExpiryDate(LocalDate localDate) {
        this.expiryDate.setValue(localDate);
    }

    public MutableLiveData<String> getBtnAddUpdateTxt() {
        return btnAddUpdateTxt;
    }

    public void setNewReminderTimeList(List<LocalDate> days, List<LocalTime> selectedTimes) {
        List<Reminder> reminderList = new ArrayList<>();

        for (LocalDate day : days) {
            for (LocalTime time : selectedTimes) {
                LocalDateTime localDateTime = LocalDateTime.of(day, time);
                reminderList.add(new Reminder(localDateTime.toEpochSecond(ZoneOffset.ofHoursMinutes(5, 45)),
                        id,
                        localDateTime,
                        "Pending",
                        isOnline ? prescribedByOnline.split("_")[0] : "Self"));
            }
        }
        reminders.setValue(reminderList);
    }

    public boolean isNotUpdate() {
        return !isUpdate;
    }

    // id, name, type, dailyIntake, imagePath, expiryDate, prescribedBy, {id, reminderTime, reminderType, assignedBy}, medicineNotes
    public void startAddUpdate() {
        Medicine latest = new Medicine(id,
                medName.getValue(),
                medType.getValue(),
                Integer.parseInt(dailyIntake.getValue()),
                imagePath.getValue(),
                expiryDate.getValue(),
                isOnline ? prescribedByOnline : prescribedBy.getValue(),
                isOnline ? prescribedTo : null,
                medicineNotes.getValue());
        if (isOnline) {
            remoteRepo.insertMedicineAndItsReminders(latest, reminders.getValue());
            snackMsg.setValue(new Event<>(R.string.reminderInsertSuccess_text));
        } else {
            if (isUpdate) {
                localRepo.updateAMedicine(latest);
                updateAllReminders(latest);
                snackMsg.setValue(new Event<>(R.string.reminderUpdateSuccess_text));
            } else {
                localRepo.insertAMedicine(latest);
                localRepo.insertAllReminders(reminders.getValue());
                snackMsg.setValue(new Event<>(R.string.reminderInsertSuccess_text));
            }
            ReminderHelper.scheduleReminder(getAppContext(), latest, reminders.getValue(), isUpdate, isOnline);
        }
    }

    private void updateAllReminders(Medicine medicine) {
        List<Reminder> pending = localRepo.getAllRemindersOfAMedicineByReminderType(medicine.getId(), "Pending");
        ReminderHelper.cancelAllReminders(getAppContext(), medicine, pending, isOnline);
        localRepo.insertAllReminders(reminders.getValue());
        ReminderHelper.scheduleReminder(getAppContext(), medicine, reminders.getValue(), isUpdate, isOnline);
    }

    // miscellaneous methods

    private Uri createImageFile(String reminderName) {
        Context context = getApplication().getApplicationContext();
        String uniqueTimeStamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String imgFileName = reminderName + "_" + uniqueTimeStamp + "_";
        File storageDir = Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        try {
            File imageFile = File.createTempFile(imgFileName, ".jpg", storageDir);
            if (!TextUtils.isEmpty(imagePath.getValue())) {
                new File(imagePath.getValue()).delete();
            }
            imagePath.setValue(imageFile.getAbsolutePath());
            return FileProvider.getUriForFile(context, "com.gbsoft.fileprovider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onActivityResult(Boolean result) {
        if (result) {
            snackMsg.setValue(new Event<>(R.string.saveImgSuccess_text));
        }
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }

    private Context getAppContext() {
        return getApplication().getApplicationContext();
    }
}

