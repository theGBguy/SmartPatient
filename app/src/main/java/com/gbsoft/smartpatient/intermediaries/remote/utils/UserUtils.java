package com.gbsoft.smartpatient.intermediaries.remote.utils;

import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.data.LoggedInUser;
import com.gbsoft.smartpatient.data.LoginDetail;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.data.RegisteredUser;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.remote.Result;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

public class UserUtils {
    private static final String TAG = "UserUtils";

    private static final String USERS_COL_REF = "users";
    private static final String USERS_DOC_ID = "doc_users";
    private static final String PATIENT_COL_REF = "patients";
    private static final String HEALTH_PERSONNEL_COL_REF = "health_personnel";

    private static final String USERS_TYPE_COL_REF = "users_type";

    private static final String NAME_FIELD = "name";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    @Inject
    public UserUtils(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, FirebaseStorage storage) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.storage = storage;
    }

    public LiveData<Result> login(LoginDetail loginDetail) {
        MutableLiveData<Result> currentUsr = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(loginDetail.getUsername(), loginDetail.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        currentUsr.postValue(new Result.Success<>(new LoggedInUser(user.getUid(), user.getDisplayName())));
                    } else {
                        StringBuilder errMsgBuilder = new StringBuilder("Error logging in : ");
                        Exception exception = task.getException();
                        Timber.tag(TAG).d(task.getException().getClass().getName());
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            errMsgBuilder.append("This user account either does not exist or has been disabled.");
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            errMsgBuilder.append("You probably entered invalid password.");
                        } else if (exception instanceof FirebaseException) {
                            errMsgBuilder.append("Please ensure that your internet connection is working properly or try again later");
                        } else {
                            errMsgBuilder.append(exception.getLocalizedMessage());
                        }
                        currentUsr.postValue(new Result.Error(errMsgBuilder.toString()));
                    }
                });

        return currentUsr;
    }

    public LiveData<Result> register(User user) {
        MutableLiveData<Result> registeredUsr = new MutableLiveData<>();

        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.setId(getUid());
                        saveUserInDb(user);
                        sendVerificationEmail();
                        registeredUsr.postValue(new Result.Success<>(new RegisteredUser(user.getEmail())));
                    } else {
                        StringBuilder errorMsgBuilder = new StringBuilder("Error registering the user : ");
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            errorMsgBuilder.append("An account with the entered email address already exists.");
                        } else {
                            errorMsgBuilder.append(exception.getLocalizedMessage());
                        }
                        registeredUsr.postValue(new Result.Error(errorMsgBuilder.toString()));
                    }
                });
        return registeredUsr;
    }

    public boolean logout() {
        if (firebaseAuth.getCurrentUser() == null) return false;
        firebaseAuth.signOut();
        return true;
    }

    private void sendVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Timber.tag(TAG).d("Email sent successfully");
                });
    }

    private void saveUserInDb(User user) {
        boolean isPatient = user instanceof Patient;

        Map<String, Object> typeMap = new HashMap<>();
        typeMap.put("type", isPatient ? "patients" : "health_personnel");

        Task<Void> saveUserTask = firestore.collection(USERS_COL_REF)
                .document(USERS_DOC_ID)
                .collection(user instanceof Patient ? PATIENT_COL_REF : HEALTH_PERSONNEL_COL_REF)
                .document(user.getId())
                .set(user);

        Task<Void> saveUserTypeTask = firestore.collection(USERS_TYPE_COL_REF)
                .document(user.getId())
                .set(typeMap);

        saveUserTask.continueWithTask(task -> saveUserTypeTask)
                .addOnSuccessListener(result -> {
                    Timber.tag(TAG).d("DocumentSnapshot added with ID: %s", user.getId());
                    logout();
                })
                .addOnFailureListener(e -> Timber.tag(TAG).d("Error adding document %s", e.getLocalizedMessage()));
    }

//    private void saveUserInfoInDb(User user) {
//        firestore.collection(USERS_COL_REF)
//                .document(USERS_DOC_ID)
//                .collection(user instanceof Patient ? PATIENT_COL_REF : HEALTH_PERSONNEL_COL_REF)
//                .document(user.getId())
//                .set(user)
//                .addOnSuccessListener(result -> Timber.tag(TAG).d("DocumentSnapshot added with ID: %s", user.getId()))
//                .addOnFailureListener(e -> Timber.tag(TAG).d("Error adding document %s", e.getLocalizedMessage()));
//    }
//
//    private void saveUserTypeInDb(User user) {
//        boolean isPatient = user instanceof Patient;
//
//        Map<String, Object> typeMap = new HashMap<>();
//        typeMap.put("type", isPatient ? "patients" : "health_personnel");
//
//        firestore.collection(USERS_TYPE_COL_REF)
//                .document(user.getId())
//                .set(typeMap)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful())
//                        Timber.d("Successfully saved the user's type");
//                    else
//                        Timber.d("Couldn't save the user's type in database : %s", task.getException().getLocalizedMessage());
//                });
//    }

    public LiveData<String> getUserType() {
        MutableLiveData<String> userType = new MutableLiveData<>();
        firestore.collection(USERS_TYPE_COL_REF)
                .document(getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        userType.setValue((String) task.getResult().get("type"));
                    else
                        Timber.tag(TAG).d("Couldn't retrieve the type of the user");
                });
        return userType;
    }

    public Query getHealthPersonnelQuery() {
        return firestore.collection(USERS_COL_REF)
                .document(USERS_DOC_ID)
                .collection(HEALTH_PERSONNEL_COL_REF)
                .orderBy(NAME_FIELD);
    }

//    public Query getPatientQuery(Query patientQuery) {
//        try {
//            Future<Query> queryFuture = AppDatabase.executorService.submit(() -> {
//                QuerySnapshot listSnapshot = Tasks.await(patientQuery.get());
//                List<DocumentSnapshot> idListDocs = listSnapshot.getDocuments();
//
//                List<String> idList = new ArrayList<>();
//                for (DocumentSnapshot docs : idListDocs) {
//                    idList.add(docs.getString("patientUid"));
//                }
//
//                return firestore.collection(USERS_COL_REF)
//                        .document(USERS_DOC_ID)
//                        .collection(PATIENT_COL_REF)
//                        .whereIn("id", idList)
//                        .orderBy(NAME_FIELD);
//            });
//            return queryFuture.get();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public LiveData<Query> getPatientQuery(Query patientQuery) {
        MutableLiveData<Query> queryLive = new MutableLiveData<>();
        patientQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> idListDocs = task.getResult().getDocuments();
                if (idListDocs.size() > 0) {
                    List<String> idList = new ArrayList<>();
                    for (DocumentSnapshot docs : idListDocs) {
                        idList.add(docs.getString("patientUid"));
                    }

                    queryLive.setValue(firestore.collection(USERS_COL_REF)
                            .document(USERS_DOC_ID)
                            .collection(PATIENT_COL_REF)
                            .whereIn("id", idList)
                            .orderBy(NAME_FIELD));
                }
            }
        });
        return queryLive;
    }

    public Query getAllPatientsQuery() {
        return firestore.collection(USERS_COL_REF)
                .document(USERS_DOC_ID)
                .collection(PATIENT_COL_REF);
    }

    public LiveData<User> getAccountDetails(String uid) {
        MutableLiveData<User> user = new MutableLiveData<>();

        if (TextUtils.isEmpty(uid)) {
            uid = getUid();
            if (TextUtils.isEmpty(uid)) return user;
        }
        String finalUid = uid;
        firestore.collection(USERS_TYPE_COL_REF)
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isPatient = TextUtils.equals(task.getResult().getString("type"), PATIENT_COL_REF);
                        firestore.collection(USERS_COL_REF)
                                .document(USERS_DOC_ID)
                                .collection(isPatient ? PATIENT_COL_REF : HEALTH_PERSONNEL_COL_REF)
                                .document(finalUid)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (isPatient)
                                        user.setValue(documentSnapshot.toObject(Patient.class));
                                    else
                                        user.setValue(documentSnapshot.toObject(HealthPersonnel.class));
                                })
                                .addOnFailureListener(e -> Timber.tag(TAG).d("Error getting account details : %s", e.getLocalizedMessage()));
                    } else {
                        Timber.tag(TAG).d("Error getting account details : %s",
                                task.getException().getLocalizedMessage());
                    }
                });
        return user;
    }

    public String getUid() {
        if (firebaseAuth == null) return "";
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return "";
        return user.getUid();
    }

    public LiveData<Result> setAvailability(String availability) {
        MutableLiveData<Result> availLive = new MutableLiveData<>();
        firestore.collection(USERS_COL_REF)
                .document(USERS_DOC_ID)
                .collection(HEALTH_PERSONNEL_COL_REF)
                .document(getUid())
                .update("availability", availability)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        availLive.setValue(new Result.Success<>(true));
                    } else
                        availLive.setValue(new Result.Error("Error updating availability : " + task.getException().getLocalizedMessage()));
                });
        return availLive;
    }

    public LiveData<Result> setPhotoUrl(Uri localPhotoUri, boolean isPatient) {
        MutableLiveData<Result> isSet = new MutableLiveData<>();
        StorageReference picRef = storage.getReference().child("/profile_pics/" + getUid() + "/profile_pic.jpg");

        picRef.putFile(localPhotoUri).continueWithTask(task -> {
            if (!task.isSuccessful())
                throw task.getException();
            return picRef.getDownloadUrl();
        }).continueWithTask(task -> {
            firestore.collection(USERS_COL_REF)
                    .document(USERS_DOC_ID)
                    .collection(isPatient ? PATIENT_COL_REF : HEALTH_PERSONNEL_COL_REF)
                    .document(getUid())
                    .update("photoUrl", task.getResult().toString());
            return task;
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isSet.setValue(new Result.Success<>(task.getResult().toString()));
            } else
                isSet.setValue(new Result.Error("Error updating profile picture : " + task.getException().getLocalizedMessage()));
        });

        return isSet;
    }

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
