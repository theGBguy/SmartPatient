package com.gbsoft.smartpatient.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.local.LocalRepo;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.utils.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends AndroidViewModel {
    private final LocalRepo localRepo;
    private final RemoteRepo remoteRepo;

    private final MutableLiveData<User> currentUserLive = new MutableLiveData<>();
    private final MutableLiveData<Integer> pendingMedicinesSize = new MutableLiveData<>();

    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    @Inject
    public MainViewModel(@NonNull Application application, LocalRepo localRepo, RemoteRepo remoteRepo) {
        super(application);
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
    }

    // do all the initialization stuffs
    public void init(boolean isSavedInstanceStateNull) {
        // setting default alarms
        if (isSavedInstanceStateNull)
            localRepo.setDefaultAlarm();

        pendingMedicinesSize.setValue(localRepo.getAllPendingReminderWithMedicines().size());
    }

    public void initializeCurrentUser() {
        if (currentUserLive.getValue() == null) {
            LiveData<User> currentUsr = remoteRepo.getAccountDetails("");
            currentUsr.observeForever(new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    currentUsr.removeObserver(this);
                    currentUserLive.setValue(user);
                }
            });
        }
    }

    public LiveData<Integer> getPendingMedicinesSize() {
        return pendingMedicinesSize;
    }

    public User getCurrentUser() {
        return currentUserLive.getValue();
    }

    public LiveData<Boolean> isCurrentUserAPatient = Transformations.map(currentUserLive, input -> {
        if (input == null) return null;
        return input instanceof Patient;
    });

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }

    public void logout() {
        currentUserLive.setValue(null);
        if (remoteRepo.logout()) {
            snackMsg.setValue(new Event<>(R.string.logOut_success));
        }
    }
}
