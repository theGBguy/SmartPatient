package com.gbsoft.smartpatient.ui.main.home.offline;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.ui.main.addupdatemed.AddUpdateMedicineFrag;

public class OfflineHomeEventHandler {
    public void onFabClick(View v, OfflineHomeViewModel viewModel) {
        viewModel.setExitTransition(true);
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(v, v.getContext().getString(R.string.from_main_to_addUpdate)).build();

        Bundle args = new Bundle();
        args.putBoolean(AddUpdateMedicineFrag.KEY_IS_ONLINE, false);
        Navigation.findNavController(v).navigate(R.id.nav_add_update_medicine, args, null, extras);
        viewModel.setExitTransition(false);
    }
}
