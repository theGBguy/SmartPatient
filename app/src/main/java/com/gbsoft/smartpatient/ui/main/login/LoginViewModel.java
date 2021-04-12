package com.gbsoft.smartpatient.ui.main.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.LoginDetail;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.intermediaries.remote.Result;
import com.gbsoft.smartpatient.utils.Event;
import com.gbsoft.smartpatient.utils.Validator;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggingIn = new MutableLiveData<>(false);

    // events
    public static final String OFFLINE_HOME = "offline_home";
    public static final String ONLINE_HOME = "online_home";
    public static final String SETTINGS = "settings";

    private final MutableLiveData<Event<String>> nextFragment = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> snackbarMsg = new MutableLiveData<>();

    private final MediatorLiveData<Boolean> isDataValid = new MediatorLiveData<>();
    private final RemoteRepo remoteRepo;

    @Inject
    LoginViewModel(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
        startObservingErrors();
    }

    public void onLoginClick() {
        // can be launched in a separate asynchronous job
        isLoggingIn.setValue(true);
        LiveData<Result> result = remoteRepo.login(new LoginDetail(username.getValue(), password.getValue()));
        result.observeForever(new Observer<Result>() {
            @Override
            public void onChanged(Result userResult) {
                result.removeObserver(this);
                isLoggingIn.setValue(false);
                if (userResult instanceof Result.Success) {
                    nextFragment.setValue(new Event<>(ONLINE_HOME));
                } else {
                    snackbarMsg.setValue(new Event<>(((Result.Error) userResult).getError()));
                }
            }
        });
    }

    public void onSwitchToOfflineClick() {
        nextFragment.setValue(new Event<>(OFFLINE_HOME));
    }

    public void onSwitchToSettingsClick() {
        nextFragment.setValue(new Event<>(SETTINGS));
    }


    public LiveData<Integer> usernameError = Transformations.map(username, input -> {
        if (Validator.isUserNameValid(input)) return null;
        return R.string.invalid_username;
    });

    public LiveData<Integer> passwordError = Transformations.map(password, input -> {
        if (Validator.isPasswordValid(input)) return null;
        return R.string.invalid_password;
    });

    private void startObservingErrors() {
        isDataValid.addSource(usernameError, aInt -> isDataValid.setValue(aInt == null && passwordError.getValue() == null));
        isDataValid.addSource(passwordError, aInt -> isDataValid.setValue(usernameError.getValue() == null && aInt == null));
    }

    public LiveData<Boolean> isDataValid() {
        return isDataValid;
    }

    public LiveData<Boolean> isLoggingIn() {
        return isLoggingIn;
    }

    public LiveData<Event<String>> getNextFragment() {
        return nextFragment;
    }

    public LiveData<Event<String>> getSnackbarMsg() {
        return snackbarMsg;
    }
}