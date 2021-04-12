package com.gbsoft.smartpatient.ui.main.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.intermediaries.remote.Result;
import com.gbsoft.smartpatient.utils.Event;
import com.gbsoft.smartpatient.utils.Validator;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HealthPersonnelRegVM extends ViewModel {
    // for 2 way data binding
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<String> qualification = new MutableLiveData<>("");
    public final MutableLiveData<String> nmc = new MutableLiveData<>("");
    public final MutableLiveData<String> speciality = new MutableLiveData<>("");
    public final MutableLiveData<String> phone = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");

    public final MutableLiveData<Integer> checkedGenderRdoBtnId = new MutableLiveData<>(R.id.rdo_others);

    private final MediatorLiveData<Boolean> isDataValid = new MediatorLiveData<>();

    //events
    private final MutableLiveData<Event<Integer>> snackbarMsg = new MutableLiveData<>();

    private final RemoteRepo remoteRepo;

    @Inject
    public HealthPersonnelRegVM(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
        startObservingErrors();
    }

    public void onBtnRegisterClick() {
        String gender;
        Integer checkedRadioId = checkedGenderRdoBtnId.getValue();
        if (checkedRadioId == R.id.rdo_male) gender = User.GENDER_MALE;
        else if (checkedRadioId == R.id.rdo_female) gender = User.GENDER_FEMALE;
        else gender = User.GENDER_OTHERS;
        User user = new HealthPersonnel(
                name.getValue(),
                qualification.getValue(),
                Long.parseLong(nmc.getValue()),
                speciality.getValue(),
                Long.parseLong(phone.getValue()),
                email.getValue(),
                password.getValue(),
                gender
        );
        LiveData<Result> result = remoteRepo.register(user);
        result.observeForever(new Observer<Result>() {
            @Override
            public void onChanged(Result registeredUserResult) {
                result.removeObserver(this);
                if (registeredUserResult instanceof Result.Success) {
                    snackbarMsg.setValue(new Event<>(R.string.registration_ok));
                } else {
                    snackbarMsg.setValue(new Event<>(R.string.registration_failed));
                }
            }
        });
    }

    public LiveData<Integer> nameError = Transformations.map(name, input -> {
        if (Validator.isTextValid(input)) return -1;
        return R.string.invalid_name;
    });

    public LiveData<Integer> qualificationError = Transformations.map(qualification, input -> {
        if (Validator.isTextValid(input)) return -1;
        return R.string.invalid_qualification;
    });

    public LiveData<Integer> nmcError = Transformations.map(nmc, input -> {
        if (Validator.isTextValid(input)) return -1;
        return R.string.invalid_nmc;
    });

    public LiveData<Integer> specialityError = Transformations.map(speciality, input -> {
        if (Validator.isTextValid(input)) return -1;
        return R.string.invalid_speciality;
    });

    public LiveData<Integer> emailError = Transformations.map(email, input -> {
        if (Validator.isUserNameValid(input)) return -1;
        return R.string.invalid_username;
    });

    public LiveData<Integer> passwordError = Transformations.map(password, input -> {
        if (Validator.isPasswordValid(input)) return -1;
        return R.string.invalid_password;
    });

    public LiveData<Integer> phoneError = Transformations.map(phone, input -> {
        if (Validator.isPhoneNumValid(input)) return -1;
        return R.string.invalid_phone;
    });

    public MediatorLiveData<Boolean> isDataValid() {
        return isDataValid;
    }

    private boolean getValidity() {
        if (nameError.getValue() != -1) return false;
        if (qualificationError.getValue() != -1) return false;
        if (nmcError.getValue() != -1) return false;
        if (specialityError.getValue() != -1) return false;
        if (emailError.getValue() != -1) return false;
        if (passwordError.getValue() != -1) return false;
        return phoneError.getValue() == -1;
    }

    public LiveData<Event<Integer>> getSnackbarMsg() {
        return snackbarMsg;
    }

    public void startObservingErrors() {
        isDataValid.addSource(nameError, aInt -> isDataValid.setValue(getValidity()));
        isDataValid.addSource(qualificationError, aInt -> isDataValid.setValue(getValidity()));
        isDataValid.addSource(nmcError, aInt -> isDataValid.setValue(getValidity()));
        isDataValid.addSource(specialityError, aInt -> isDataValid.setValue(getValidity()));
        isDataValid.addSource(emailError, aInt -> isDataValid.setValue(getValidity()));
        isDataValid.addSource(passwordError, aInt -> isDataValid.setValue(getValidity()));
        isDataValid.addSource(phoneError, aInt -> isDataValid.setValue(getValidity()));
    }
}
