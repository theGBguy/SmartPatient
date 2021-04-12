package com.gbsoft.smartpatient.ui.main.home.offline;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OfflineHomeViewModel extends ViewModel {
    private final MutableLiveData<Boolean> exitTransition = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentTabPos = new MutableLiveData<>(0);

    @Inject
    public OfflineHomeViewModel() {
    }

    public LiveData<Boolean> getSetExitTransition() {
        return exitTransition;
    }

    public void setExitTransition(boolean should) {
        exitTransition.setValue(should);
    }

    public LiveData<Integer> getCurrentTabPos() {
        return currentTabPos;
    }

    public LiveData<Integer> getCurrentTabPosLiveData() {
        return currentTabPos;
    }

    public void setCurrentTabPos(int pos) {
        currentTabPos.setValue(pos);
    }
}
