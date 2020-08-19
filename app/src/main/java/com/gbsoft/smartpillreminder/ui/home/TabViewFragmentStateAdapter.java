package com.gbsoft.smartpillreminder.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gbsoft.smartpillreminder.ui.reminderslist.RemindersListFragment;

/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabViewFragmentStateAdapter extends FragmentStateAdapter {

    TabViewFragmentStateAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return RemindersListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}