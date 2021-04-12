package com.gbsoft.smartpatient.intermediaries.remote.utils;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.utils.CustomMapper;
import com.gbsoft.smartpatient.utils.ReminderHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import timber.log.Timber;

public class MedicineUtils {
    private static final String TAG = "MedicineUtils";

    private static final String MEDICINES_COL_REF = "medicines";
    private static final String MEDICINES_DOC_REF = "doc_medicines";
    private static final String MEDICINES_SUB_COL_REF = "sub_col_medicines";
    private static final String REMINDERS_COL_REF = "reminders";
    private static final String REMINDERS_SUB_COL_REF = "sub_col_reminders";

    private static final String PATIENT_MEDICINES_DATA_COL_REF = "patient_medicines_data";
    private static final String PATIENT_MEDICINES_SUB_COL_REF = "sub_col_patient_medicines";

    private static final String ID_FIELD = "id";
    private static final String REMINDER_ID_FIELD = "reminderId";
    private static final String REMINDER_TYPE_FIELD = "reminderType";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final Context context;

    @Inject
    public MedicineUtils(@ApplicationContext Context context, FirebaseAuth firebaseAuth, FirebaseFirestore firestore, FirebaseStorage storage) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.storage = storage;
    }

    public void insertMedicineAndItsReminders(Medicine medicine, List<Reminder> reminders) {
        Map<String, Object> medicineId = new HashMap<>();
        medicineId.put("medicineId", medicine.getId());

        Task<Void> insertMedicine = firestore.collection(MEDICINES_COL_REF)
                .document(MEDICINES_DOC_REF)
                .collection(MEDICINES_SUB_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .set(CustomMapper.getMapFromMedicine(medicine));

        StorageReference picRef = storage.getReference().child("/med_pics/" + medicine.getId() + "/" + medicine.getName() + ".jpg");

        Task<Uri> imgUploadTask = picRef.putFile(Uri.fromFile(new File(medicine.getImagePath())))
                .continueWithTask(task -> {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return picRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    firestore.collection(MEDICINES_COL_REF)
                            .document(MEDICINES_DOC_REF)
                            .collection(MEDICINES_SUB_COL_REF)
                            .document(String.valueOf(medicine.getId()))
                            .update("imagePath", task.getResult().toString());
                    return task;
                });

        Task<Void> insertMedicineId = firestore.collection(PATIENT_MEDICINES_DATA_COL_REF)
                .document(medicine.getPrescribedTo())
                .collection(PATIENT_MEDICINES_SUB_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .set(medicineId);

        insertMedicine.continueWithTask(task -> insertMedicineId)
                .continueWithTask(task -> imgUploadTask)
                .continueWithTask(task -> {
                    for (Reminder reminder : reminders) {
                        insertReminder(reminder);
                    }
                    return null;
                })
                .addOnSuccessListener(docRef -> Timber.tag(TAG).d("Successfully inserted medicines and its reminders"))
                .addOnFailureListener(e -> Timber.tag(TAG).d("Medicine insertion failed: %s", e.getLocalizedMessage()));
    }

    public LiveData<Boolean> deleteMedicineAndItsReminders(Medicine medicine, boolean isHp) {
        MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
        Task<Void> deleteMedicine = firestore.collection(MEDICINES_COL_REF)
                .document(MEDICINES_DOC_REF)
                .collection(MEDICINES_SUB_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .delete();

        Task<Void> deleteImage = storage.getReference().
                child("/med_pics/" + medicine.getId() + "/" + medicine.getName() + ".jpg")
                .delete();

        Task<Void> deleteMedicineId = firestore.collection(PATIENT_MEDICINES_DATA_COL_REF)
                .document(medicine.getPrescribedTo())
                .collection(PATIENT_MEDICINES_SUB_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .delete();

        if (isHp) {
            deleteMedicine.continueWithTask(task -> deleteMedicine)
                    .continueWithTask(task -> deleteImage)
                    .continueWithTask(task -> deleteMedicineId)
                    .addOnSuccessListener(docRef -> isDeleted.setValue(true))
                    .addOnFailureListener(e -> isDeleted.setValue(false));
        } else {
            Task<Void> deleteReminders = firestore.collection(REMINDERS_COL_REF)
                    .document(String.valueOf(medicine.getId()))
                    .delete();

            Task<QuerySnapshot> getPendingReminders = firestore.collection(REMINDERS_COL_REF)
                    .document(String.valueOf(medicine.getId()))
                    .collection(REMINDERS_SUB_COL_REF)
                    .whereEqualTo(REMINDER_TYPE_FIELD, "Pending")
                    .orderBy(REMINDER_ID_FIELD)
                    .get();

            deleteMedicine.continueWithTask(task -> deleteMedicine)
                    .continueWithTask(task -> deleteImage)
                    .continueWithTask(task -> deleteMedicineId)
                    .continueWithTask(task -> getPendingReminders)
                    .continueWithTask(task -> {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Reminder> reminders = new ArrayList<>();
                        for (DocumentSnapshot doc : documents) {
                            reminders.add(CustomMapper.getReminderFromSnapshot(doc));
                        }
                        ReminderHelper.cancelAllReminders(context, medicine, reminders, true);
                        return null;
                    })
                    .continueWithTask(task -> deleteReminders)
                    .addOnSuccessListener(docRef -> isDeleted.setValue(true))
                    .addOnFailureListener(e -> isDeleted.setValue(false));
        }
        return isDeleted;
    }

    public void cancelAllPendingReminders(Medicine medicine) {
        Task<Void> deleteReminders = firestore.collection(REMINDERS_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .delete();

        Task<QuerySnapshot> getPendingReminders = firestore.collection(REMINDERS_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .collection(REMINDERS_SUB_COL_REF)
                .whereEqualTo(REMINDER_TYPE_FIELD, "Pending")
                .orderBy(REMINDER_ID_FIELD)
                .get();

        getPendingReminders
                .continueWithTask(task -> {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    List<Reminder> reminders = new ArrayList<>();
                    for (DocumentSnapshot doc : documents) {
                        reminders.add(CustomMapper.getReminderFromSnapshot(doc));
                    }
                    ReminderHelper.cancelAllReminders(context, medicine, reminders, true);
                    return null;
                })
                .continueWithTask(task -> deleteReminders)
                .addOnSuccessListener(task -> Timber.tag(TAG).d("Successfully deleted and cancelled the pending reminders"))
                .addOnFailureListener(e -> Timber.tag(TAG).d("Error occurred while fetching reminders %s", e.getLocalizedMessage()));
    }

    private void insertReminder(Reminder reminder) {
        firestore.collection(REMINDERS_COL_REF)
                .document(String.valueOf(reminder.getMedicineId()))
                .collection(REMINDERS_SUB_COL_REF)
                .document(String.valueOf(reminder.getReminderId()))
                .set(reminder);
    }

    public Query getHPMedicinesQuery(String queryString) {
        return firestore.collection(MEDICINES_COL_REF)
                .document(MEDICINES_DOC_REF)
                .collection(MEDICINES_SUB_COL_REF)
                .whereEqualTo("prescribedBy", queryString)
                .orderBy(ID_FIELD, Query.Direction.DESCENDING);
    }

    public Query getPatientMedicinesQuery(String queryString) {
        return firestore.collection(MEDICINES_COL_REF)
                .document(MEDICINES_DOC_REF)
                .collection(MEDICINES_SUB_COL_REF)
                .whereEqualTo("prescribedTo", queryString)
                .orderBy(ID_FIELD, Query.Direction.DESCENDING);
    }

    public Query getMedicineIdList() {
        return firestore.collection(PATIENT_MEDICINES_DATA_COL_REF)
                .document(getUid())
                .collection(PATIENT_MEDICINES_SUB_COL_REF);
    }

    public LiveData<List<Reminder>> getRemindersOfAMedicine(long medicineId) {
        MutableLiveData<List<Reminder>> remindersLive = new MutableLiveData<>();
        firestore.collection(REMINDERS_COL_REF)
                .document(String.valueOf(medicineId))
                .collection(REMINDERS_SUB_COL_REF)
                .orderBy(REMINDER_ID_FIELD)
                .get()
                .addOnSuccessListener(task -> {
                    List<DocumentSnapshot> documents = task.getDocuments();
                    List<Reminder> reminders = new ArrayList<>();
                    for (DocumentSnapshot doc : documents) {
                        reminders.add(CustomMapper.getReminderFromSnapshot(doc));
                    }
                    remindersLive.setValue(reminders);
                })
                .addOnFailureListener(e -> Timber.tag(TAG).d("Error occurred while fetching reminders : %s", e.getLocalizedMessage()));
        return remindersLive;
    }

    public void schedulePendingReminders(Medicine medicine) {
        firestore.collection(REMINDERS_COL_REF)
                .document(String.valueOf(medicine.getId()))
                .collection(REMINDERS_SUB_COL_REF)
                .whereEqualTo(REMINDER_TYPE_FIELD, "Pending")
                .orderBy(REMINDER_ID_FIELD)
                .get()
                .addOnSuccessListener(task -> {
                    List<DocumentSnapshot> documents = task.getDocuments();
                    List<Reminder> reminders = new ArrayList<>();
                    for (DocumentSnapshot doc : documents) {
                        reminders.add(CustomMapper.getReminderFromSnapshot(doc));
                    }
                    ReminderHelper.scheduleReminder(context, medicine, reminders, false, true);
                })
                .addOnFailureListener(e -> Timber.tag(TAG).d("Error occurred while fetching reminders %s", e.getLocalizedMessage()));
    }

    public void updateAReminder(Reminder reminder) {
        firestore.collection(REMINDERS_COL_REF)
                .document(String.valueOf(reminder.getMedicineId()))
                .collection(REMINDERS_SUB_COL_REF)
                .document(String.valueOf(reminder.getReminderId()))
                .update("reminderType", reminder.getReminderType())
                .addOnSuccessListener((task) -> Timber.tag(TAG).d("Reminder type updated successfully."))
                .addOnFailureListener((e) -> Timber.tag(TAG).d("Error occurred while updating reminder type : %s", e.getLocalizedMessage()));
    }

    private String getUid() {
        if (firebaseAuth == null) return "";
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return "";
        return user.getUid();
    }
}
