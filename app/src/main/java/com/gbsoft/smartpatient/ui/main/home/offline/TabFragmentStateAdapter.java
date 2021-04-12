package com.gbsoft.smartpatient.ui.main.home.offline;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gbsoft.smartpatient.ui.main.medicinelist.MedicineListFrag;

/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabFragmentStateAdapter extends FragmentStateAdapter {

    TabFragmentStateAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MedicineListFrag.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}