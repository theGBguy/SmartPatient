package com.gbsoft.smartpatient.ui.main.profile;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.intermediaries.remote.Result;
import com.gbsoft.smartpatient.utils.Event;

import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private RemoteRepo remoteRepo;
    private String uid;
    private String phoneNum, email;

    private final MutableLiveData<User> userMutable = new MutableLiveData<>();
    private final MutableLiveData<Boolean> shouldHide = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> fabRes = new MutableLiveData<>(R.drawable.ic_baseline_contact_support_24);
    private final MutableLiveData<Integer> fabTxt = new MutableLiveData<>();
    public final MutableLiveData<String> availability = new MutableLiveData<>();

    // events
    public static final String EVENT_CALL = "call";
    public static final String EVENT_EMAIL = "email";
    public static final String EVENT_ADD_PROFILE_PIC = "profile_pic";
    public static final String EVENT_UPLOADING_PIC = "uploading_pic";
    public static final String EVENT_UPLOADING_PIC_COMPLETED = "uploading_pic_completed";
    public static final String EVENT_NEW_APPOINTMENT = "new_appointment";
    private final MutableLiveData<Event<String>> events = new MutableLiveData<>();

    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
    }

    public void setVariables(Bundle args) {
        if (args == null)
            this.uid = "";
        else
            this.uid = args.getString(ProfileFragment.KEY_UID, "");

        LiveData<User> user = remoteRepo.getAccountDetails(uid);
        user.observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user1) {
                user.removeObserver(this);
                if (user1 == null) {
                    snackMsg.setValue(new Event<>(R.string.user_fetch_error));
                    return;
                }
                userMutable.setValue(user1);
                phoneNum = String.valueOf(user1.getPhoneNum());
                email = user1.getEmail();
                if (user1 instanceof HealthPersonnel)
                    availability.setValue(((HealthPersonnel) user1).getAvailability());
                else
                    availability.setValue("(unspecified)");
                fabTxt.setValue(R.string.fabOptions_txt);
            }
        });
    }

    public LiveData<Boolean> isPatient = Transformations.map(userMutable, input -> input instanceof Patient);

    public LiveData<Boolean> isHp = Transformations.map(userMutable, input -> input instanceof HealthPersonnel);

    public LiveData<Boolean> isSelfAccount = Transformations.map(userMutable, input -> {
        if (remoteRepo == null) return false;
        return TextUtils.equals(input.getId(), remoteRepo.getUid());
    });

    public LiveData<String> getUserName = Transformations.map(userMutable, User::getName);

    public LiveData<String> getGender = Transformations.map(userMutable, input -> String.format(Locale.getDefault(),
            "Gender : %s", input.getGender()));

    public LiveData<String> getAge = Transformations.map(userMutable, input -> {
        String age = input instanceof Patient ? String.valueOf(((Patient) input).getAge()) : "(unspecified)";
        return String.format(Locale.getDefault(),
                "Age : %s", age);
    });

    public LiveData<String> getAddress = Transformations.map(userMutable, input -> {
        String address = input instanceof Patient ? ((Patient) input).getAddress() : "(unspecified)";
        return String.format(Locale.getDefault(),
                "Address : %s", address);
    });

    public LiveData<String> getNMC = Transformations.map(userMutable, input -> {
        String nmc = input instanceof HealthPersonnel ? String.valueOf(((HealthPersonnel) input).getNmcNumber()) : "(unspecified)";
        return String.format(Locale.getDefault(),
                "NMC Number : %s", nmc);
    });

    public LiveData<String> getQualification = Transformations.map(userMutable, input -> {
        String qualification = input instanceof HealthPersonnel ? ((HealthPersonnel) input).getQualification() : "(unspecified)";
        return String.format(Locale.getDefault(),
                "Qualification : %s", qualification);
    });

    public LiveData<String> getSpeciality = Transformations.map(userMutable, input -> {
        String speciality = input instanceof HealthPersonnel ? ((HealthPersonnel) input).getSpeciality() : "(unspecified)";
        return String.format(Locale.getDefault(),
                "Speciality : %s", speciality);
    });

    public LiveData<String> photoUrl = Transformations.map(userMutable, User::getPhotoUrl);

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public LiveData<Boolean> shouldHide() {
        return shouldHide;
    }

    public void setAvailability() {
        LiveData<Result> availResult = remoteRepo.setAvailability(availability.getValue());
        availResult.observeForever(new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                availResult.removeObserver(this);
                if (result instanceof Result.Success)
                    snackMsg.setValue(new Event<>(R.string.avail_update_successful));
                else
                    snackMsg.setValue(new Event<>(R.string.avail_update_failed));
            }
        });
    }

    public void setPhotoUrl(Uri localUri) {
        events.setValue(new Event<>(EVENT_UPLOADING_PIC));
        LiveData<Result> uploadResult = remoteRepo.setPhotoUrl(localUri, userMutable.getValue() instanceof Patient);
        uploadResult.observeForever(new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                uploadResult.removeObserver(this);
                events.setValue(new Event<>(EVENT_UPLOADING_PIC_COMPLETED));
                if (result instanceof Result.Success) {
                    User updated = userMutable.getValue();
                    updated.setPhotoUrl(((Result.Success) result).getData().toString());
                    userMutable.setValue(updated);
                    snackMsg.setValue(new Event<>(R.string.pic_upload_successful));
                } else
                    snackMsg.setValue(new Event<>(R.string.pic_upload_failed));
            }
        });
    }

    public void onFabContactSupportClick() {
        shouldHide.setValue(!shouldHide.getValue());
        fabRes.setValue(shouldHide.getValue() ? R.drawable.ic_baseline_contact_support_24 : R.drawable.ic_baseline_clear_24);
        fabTxt.setValue(shouldHide.getValue() ? R.string.fabOptions_txt : R.string.fabOptions_txt_alt);
    }

    public void onFabPhoneClick() {
        onFabContactSupportClick();
        events.setValue(new Event<>(EVENT_CALL));
    }

    public void onFabEmailClick() {
        onFabContactSupportClick();
        events.setValue(new Event<>(EVENT_EMAIL));
    }

    public void onFabNewAppointmentClick() {
        onFabContactSupportClick();
        events.setValue(new Event<>(EVENT_NEW_APPOINTMENT));
    }

    public String getUid() {
        return uid;
    }

    public void onAddProfilePicClick() {
        events.setValue(new Event<>(EVENT_ADD_PROFILE_PIC));
    }

    public LiveData<Integer> getFabRes() {
        return fabRes;
    }

    public LiveData<Integer> getFabTxt() {
        return fabTxt;
    }

    public LiveData<Event<String>> getEvents() {
        return events;
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }
}