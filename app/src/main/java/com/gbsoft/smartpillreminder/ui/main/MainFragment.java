package com.gbsoft.smartpillreminder.ui.main;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentMainBinding;
import com.gbsoft.smartpillreminder.ui.addorupdate.AddOrUpdateReminderFragment;
import com.gbsoft.smartpillreminder.ui.settings.SettingsFragment;
import com.gbsoft.smartpillreminder.utils.Helper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialFade;

import java.util.Objects;

public class MainFragment extends Fragment implements View.OnClickListener {
    public static boolean PERMISSION_GRANTED = true;
    private ExtendedFloatingActionButton fab;
    private FragmentMainBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = binding.toolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getContext(), getChildFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(tabViewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabs.getTabAt(0)).select();
        checkForAllPermissions();

        fab = binding.fab;
        fab.setOnClickListener(this);

        final AnimatedVectorDrawableCompat pendingReminder = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.pending_reminder);
        final AnimatedVectorDrawableCompat completedReminder = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.completed_reminder);
        final AnimatedVectorDrawableCompat missedReminder = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.missed_reminder);
        tabs.getTabAt(0).setIcon(pendingReminder);
        pendingReminder.start();
        tabs.getTabAt(1).setIcon(completedReminder);
        completedReminder.start();
        tabs.getTabAt(2).setIcon(missedReminder);
        missedReminder.start();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    if (!pendingReminder.isRunning())
                        pendingReminder.start();
                    fab.show();
                } else {
                    fab.hide();
                    if (tab.getPosition() == 1) {
                        if (!completedReminder.isRunning())
                            completedReminder.start();
                    } else {
                        if (!missedReminder.isRunning())
                            missedReminder.start();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            setExitTransition(new Hold());
            AddOrUpdateReminderFragment fragment = new AddOrUpdateReminderFragment();
            String BACKSTACK_NAME = "main_to_addOrUpdate";
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .addSharedElement(fab, getString(R.string.from_main_to_addUpdate))
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(BACKSTACK_NAME)
                    .commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            setExitTransition(new MaterialFade());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(android.R.id.content, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkForAllPermissions() {
        Helper.PermissionHelper helper = new Helper.PermissionHelper();
        String[] permissionArr = new String[]{Manifest.permission.WAKE_LOCK, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String permission : permissionArr) {
            PERMISSION_GRANTED = PERMISSION_GRANTED && helper.checkPermission(requireActivity(), permission);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fab = null;
        binding = null;
    }
}
