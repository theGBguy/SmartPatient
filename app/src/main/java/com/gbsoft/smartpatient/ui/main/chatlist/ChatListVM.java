package com.gbsoft.smartpatient.ui.main.chatlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatListVM extends ViewModel {
    private final MutableLiveData<Boolean> isPatient = new MutableLiveData<>();

    private final RemoteRepo remoteRepo;

    @Inject
    public ChatListVM(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
    }

    public Query getChatIdQuery(boolean isHp) {
        return remoteRepo.getChatIdQuery(isHp);
    }

    public LiveData<Boolean> isPatient() {
        return isPatient;
    }

    public void setIsPatient(Boolean isPatient) {
        this.isPatient.setValue(isPatient);
    }

    public LiveData<Integer> availableListTxt = Transformations.map(isPatient, input -> {
        if (input == null) return 0;
        return input ? R.string.tvAvailableList_txt_p : R.string.tvAvailableList_txt_hp;
    });

    public LiveData<Integer> noUsrTxt = Transformations.map(isPatient, input -> {
        if (input == null) return 0;
        return input ? R.string.tv_no_hp_txt : R.string.tv_no_patient_txt;
    });


    public LiveData<Query> getPatientQuery() {
        return remoteRepo.getPatientQuery();
    }

    public Query getHealthPersonnelQuery() {
        return remoteRepo.getHealthPersonnelQuery();
    }

    public Query getAllPatientsQuery() {
        return remoteRepo.getAllPatientsQuery();
    }
}
