package com.gbsoft.smartpatient.ui.main.newapp;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.gbsoft.smartpatient.utils.Event;
import com.gbsoft.smartpatient.utils.TimeHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

@HiltViewModel
public class NewAppointmentViewModel extends AndroidViewModel {

    public MutableLiveData<String> appointmentName = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> appointmentTime = new MutableLiveData<>();
    private String hpId;
    private String hpName;
    private String availability;
    private String patientId;
    private String patientName;

    // variables related to events
    private final MutableLiveData<Event<Boolean>> shouldNavigateBack = new MutableLiveData<>();
    private final MutableLiveData<Event<Integer>> snackMsg = new MutableLiveData<>();

    private final RemoteRepo remoteRepo;

    @Inject
    public NewAppointmentViewModel(@NonNull Application application, RemoteRepo remoteRepo) {
        super(application);
        this.remoteRepo = remoteRepo;
    }

    public LiveData<Event<Boolean>> shouldNavigateBack() {
        return shouldNavigateBack;
    }

    public void onCancelClick() {
        shouldNavigateBack.setValue(new Event<>(true));
    }

    public void setVariables(Bundle args, MainViewModel mvm) {
        if (args != null) {
            hpId = args.getString(NewAppointmentFrag.KEY_ID);
            hpName = args.getString(NewAppointmentFrag.KEY_NAME);
            availability = args.getString(NewAppointmentFrag.KEY_AVAILABILITY);
        } else {
            hpId = "";
            hpName = "";
            availability = "";
        }
        patientId = mvm.getCurrentUser().getId();
        patientName = mvm.getCurrentUser().getName();
        appointmentTime.setValue(null);
    }

    public String getHpName() {
        return hpName;
    }

    public String getAvailabilityStr() {
        if (TextUtils.equals(availability, "(unspecified)")) {
            return hpName + " hasn't updated his/her availability status. You can try contacting " +
                    "him/her from the profile screen.";
        }
        return "Note : " + hpName + " is only available from " + availability + ". So, you should take" +
                " appointment in that time frame only.";
    }

    public LiveData<String> getAppointmentTime() {
        return Transformations.map(appointmentTime, input -> {
            if (input == null)
                return getApplication().getApplicationContext().getString(R.string.btnSetAppointmentTime_Text);
            return TimeHelper.formatLocalDateTime(input);
        });
    }

    public void setAppointmentTime(Date date) {
        Timber.v(date.toString());
        appointmentTime.setValue(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public LiveData<Event<Integer>> getSnackMsg() {
        return snackMsg;
    }

    public void onAddAppointmentClick() {
        Appointment appointment = new Appointment(
                System.currentTimeMillis(),
                appointmentName.getValue(),
                patientId,
                patientName,
                hpId,
                hpName,
                appointmentTime.getValue()
        );
        remoteRepo.insertAppointment(appointment);
        snackMsg.setValue(new Event<>(R.string.insertAppointmentSuccess_txt));
    }
}