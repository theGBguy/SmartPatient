package com.gbsoft.smartpatient.intermediaries.remote.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.intermediaries.remote.Result;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

public class AppointmentUtils {
    private static final String TAG = "AppointmentUtils";

    private static final String APPOINTMENT_COL_REF = "appointments";
    private static final String APPOINTMENT_DOC_REF = "doc_appointments";
    private static final String APPOINTMENT_SUB_COL_REF = "sub_col_appointments";

    private static final String HP_APPOINTMENT_DATA_COL_REF = "hp_appointments_data";
    private static final String HP_APPOINTMENT_SUB_COL_REF = "sub_col_hp_appointments";
    private static final String HP_PATIENT_SUB_COL_REF = "sub_col_hp_patients";

    private static final String NAME_FIELD = "name";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    @Inject
    public AppointmentUtils(FirebaseAuth firebaseAuth, FirebaseFirestore firestore) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
    }

    public void insertAppointment(Appointment appointment) {
        Map<String, Object> patientUid = new HashMap<>();
        patientUid.put("patientUid", getUid());
        if (appointment.getPatientId() == null)
            appointment.setPatientId(getUid());

        Task<Void> insertApp = firestore.collection(APPOINTMENT_COL_REF)
                .document(APPOINTMENT_DOC_REF)
                .collection(APPOINTMENT_SUB_COL_REF)
                .document(String.valueOf(appointment.getId()))
                .set(appointment);

        Task<Void> insertPatientId = firestore.collection(HP_APPOINTMENT_DATA_COL_REF)
                .document(appointment.getHpId())
                .collection(HP_PATIENT_SUB_COL_REF)
                .document(getUid())
                .set(patientUid);

        insertApp.continueWithTask(task -> insertPatientId)
                .addOnSuccessListener(docRef -> Timber.tag(TAG).d("Successfully inserted an appointment"))
                .addOnFailureListener(e -> Timber.tag(TAG).d("Appointment insertion failed: %s", e.getLocalizedMessage()));
    }

    public Query getPatientSpecificAppQuery() {
        return firestore.collection(APPOINTMENT_COL_REF)
                .document(APPOINTMENT_DOC_REF)
                .collection(APPOINTMENT_SUB_COL_REF)
                .whereEqualTo("patientId", getUid())
                .orderBy(NAME_FIELD);
    }

    public Query getHpSpecificAppQuery() {
        return firestore.collection(APPOINTMENT_COL_REF)
                .document(APPOINTMENT_DOC_REF)
                .collection(APPOINTMENT_SUB_COL_REF)
                .whereEqualTo("hpId", getUid())
                .orderBy(NAME_FIELD);
    }

    public Query getPatientIdList() {
        return firestore.collection(HP_APPOINTMENT_DATA_COL_REF)
                .document(getUid())
                .collection(HP_PATIENT_SUB_COL_REF);
    }

    public LiveData<Result> setApprovalStatus(long appId, boolean isApproved) {
        MutableLiveData<Result> approvalStatusLive = new MutableLiveData<>();
        firestore.collection(APPOINTMENT_COL_REF)
                .document(APPOINTMENT_DOC_REF)
                .collection(APPOINTMENT_SUB_COL_REF)
                .document(String.valueOf(appId))
                .update("approved", isApproved)
                .addOnSuccessListener(aVoid -> approvalStatusLive.setValue(new Result.Success<>(true)))
                .addOnFailureListener(e -> approvalStatusLive.setValue(new Result.Error("Error updating approval status : " + e.getLocalizedMessage())));
        return approvalStatusLive;
    }


    private String getUid() {
        if (firebaseAuth == null) return "";
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return "";
        return user.getUid();
    }
}
