package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.ui.main.addupdatemed.AddUpdateMedicineFrag;
import com.gbsoft.smartpatient.ui.main.chatdetails.ChatDetailsFrag;
import com.gbsoft.smartpatient.ui.main.profile.ProfileFragment;

public class PatientVM {
    public static final String ON_CLICK_TO_PROFILE = "profile";
    public static final String ON_CLICK_TO_ADD_UPDATE = "add_update";
    public static final String ON_CLICK_TO_CHAT_DETAILS = "chat_details";

    private final Patient patient;
    private final String onClickMode;

    public PatientVM(Patient patient, String onClickMode) {
        this.patient = patient;
        this.onClickMode = onClickMode;
    }

    public Patient getPatient() {
        return patient;
    }

    public void onCardClick(View card) {
        Bundle args = new Bundle();
        if (TextUtils.equals(onClickMode, ON_CLICK_TO_PROFILE)) {
            args.putString(ProfileFragment.KEY_UID, patient.getId());
            Navigation.findNavController(card).navigate(R.id.nav_profile, args, null, null);
        } else if (TextUtils.equals(onClickMode, ON_CLICK_TO_ADD_UPDATE)) {
            args.putString(AddUpdateMedicineFrag.KEY_PATIENT_UID, patient.getId());
            args.putString(AddUpdateMedicineFrag.KEY_PATIENT_NAME, patient.getName());
            args.putBoolean(AddUpdateMedicineFrag.KEY_IS_ONLINE, true);
            Navigation.findNavController(card).navigate(R.id.nav_add_update_medicine, args, null, null);
        } else {
            args.putString(ChatDetailsFrag.KEY_RECEIVER_INFO, patient.getId() + "_" + patient.getName());
            Navigation.findNavController(card).navigate(R.id.nav_chat_details, args, null, null);
        }
    }
}
