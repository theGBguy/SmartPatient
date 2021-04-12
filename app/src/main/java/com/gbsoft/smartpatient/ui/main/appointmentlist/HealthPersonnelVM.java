package com.gbsoft.smartpatient.ui.main.appointmentlist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.ui.main.chatdetails.ChatDetailsFrag;
import com.gbsoft.smartpatient.ui.main.profile.ProfileFragment;

public class HealthPersonnelVM {
    private final HealthPersonnel hp;
    private final String onClickMode;

    public HealthPersonnelVM(HealthPersonnel hp, String onClickMode) {
        this.hp = hp;
        this.onClickMode = onClickMode;
    }

    public HealthPersonnel getHp() {
        return hp;
    }

    public void onCardClick(View card) {
        Bundle args = new Bundle();
        if (TextUtils.equals(onClickMode, PatientVM.ON_CLICK_TO_PROFILE)) {
            args.putString(ProfileFragment.KEY_UID, hp.getId());
            Navigation.findNavController(card).navigate(R.id.nav_profile, args, null, null);
        } else if (TextUtils.equals(onClickMode, PatientVM.ON_CLICK_TO_CHAT_DETAILS)) {
            args.putString(ChatDetailsFrag.KEY_RECEIVER_INFO, hp.getId() + "_" + hp.getName());
            Navigation.findNavController(card).navigate(R.id.nav_chat_details, args, null, null);
        }
    }
}
