package com.gbsoft.smartpatient.ui.main.appointmentlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.intermediaries.remote.Result;
import com.gbsoft.smartpatient.utils.Event;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppointmentListVM extends ViewModel {
    private final MutableLiveData<Boolean> isHealthPersonnel = new MutableLiveData<>(false);
    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();
    private final RemoteRepo remoteRepo;

    @Inject
    public AppointmentListVM(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
    }

    public void setIsHealthPersonnel(boolean isHealthPersonnel) {
        this.isHealthPersonnel.setValue(isHealthPersonnel);
    }

    public void setApprovalStatus(long appId, boolean isApproved) {
        LiveData<Result> appApprovalResult = remoteRepo.setApprovalStatus(appId, isApproved);
        appApprovalResult.observeForever(new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                appApprovalResult.removeObserver(this);
                if (result instanceof Result.Success)
                    snackMsg.setValue(new Event<>(R.string.approval_update_successful));
                else
                    snackMsg.setValue(new Event<>(R.string.approval_update_failed));
            }
        });
    }

    public LiveData<Integer> firstListLabel = Transformations.map(isHealthPersonnel,
            isTrue -> isTrue ? R.string.rvPList_label_txt : R.string.rvHpList_label_txt);

    public LiveData<Integer> noUsrTxt = Transformations.map(isHealthPersonnel,
            isTrue -> isTrue ? R.string.tv_no_patient_txt : R.string.tv_no_hp_txt);

    public Query getHealthPersonnelQuery() {
        return remoteRepo.getHealthPersonnelQuery();
    }

    public LiveData<Query> getPatientQuery() {
        return remoteRepo.getPatientQuery();
    }

    public Query getPatientSpecificAppQuery() {
        return remoteRepo.getPatientSpecificAppQuery();
    }

    public Query getHpSpecificAppQuery() {
        return remoteRepo.getHpSpecificAppQuery();
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }
}
