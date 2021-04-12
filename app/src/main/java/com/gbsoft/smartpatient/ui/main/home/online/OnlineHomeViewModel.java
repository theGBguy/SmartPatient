package com.gbsoft.smartpatient.ui.main.home.online;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.utils.Event;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OnlineHomeViewModel extends AndroidViewModel {

    private final MutableLiveData<Event<Boolean>> shouldNavigateToLogin = new MutableLiveData<>();
    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isHp = new MutableLiveData<>(false);
    private final RemoteRepo remoteRepo;

    @Inject
    public OnlineHomeViewModel(@NonNull Application application, RemoteRepo remoteRepo) {
        super(application);
        this.remoteRepo = remoteRepo;
    }

    public void init() {
        shouldNavigateToLogin.setValue(new Event<>(!remoteRepo.isLoggedIn()));
    }

    public LiveData<Event<Boolean>> shouldNavigateToLogin() {
        return shouldNavigateToLogin;
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }

    public LiveData<Boolean> getIsHp() {
        return isHp;
    }

    public void setIsHp(boolean isHp) {
        this.isHp.setValue(isHp);
    }

    public LiveData<Integer> medicineListLabelTxt = Transformations.map(isHp, isHp -> {
        if (isHp)
            return R.string.tv_medicines_list_txt_hp;
        else return R.string.tv_medicines_list_txt_p;
    });

    public LiveData<Query> getPatientQuery() {
        return remoteRepo.getPatientQuery();
    }

    public Query getHPMedicinesQuery(String queryString) {
        return remoteRepo.getHPMedicinesQuery(queryString);
    }

    public Query getPatientMedicinesQuery(String queryString) {
        return remoteRepo.getPatientMedicinesQuery(queryString);
    }

    public void schedulePendingReminders(Medicine medicine) {
        remoteRepo.schedulePendingReminders(medicine);
    }

    public void cancelAllPendingReminders(Medicine medicine) {
        remoteRepo.cancelAllPendingReminders(medicine);
    }
}
