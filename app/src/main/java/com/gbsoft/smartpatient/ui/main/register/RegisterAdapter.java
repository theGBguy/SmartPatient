package com.gbsoft.smartpatient.ui.main.register;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RegisterAdapter extends FragmentStateAdapter {
    public RegisterAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PatientRegisterFrag();
        }
        return new HealthPersonnelRegisterFrag();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
