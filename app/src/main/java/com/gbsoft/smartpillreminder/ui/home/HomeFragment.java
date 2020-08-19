package com.gbsoft.smartpillreminder.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.ui.NavigationUI;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentHomeBinding;
import com.gbsoft.smartpillreminder.ui.MainActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.transition.Hold;

import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ExtendedFloatingActionButton fab;
    private FragmentHomeBinding binding;
    private TabLayoutMediator mediator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.homeToolbar);
        NavigationUI.setupWithNavController(binding.homeToolbar, Navigation.findNavController(requireView()),
                ((MainActivity) requireActivity()).getAppBarConfig());

        TabViewFragmentStateAdapter tabViewFragmentStateAdapter = new TabViewFragmentStateAdapter(getChildFragmentManager(),
                getViewLifecycleOwner().getLifecycle());
        ViewPager2 viewPager2 = binding.homeViewPager;
        viewPager2.setAdapter(tabViewFragmentStateAdapter);

        TabLayout tabs = binding.homeTabs;
        final String[] reminderTypeArray = getResources().getStringArray(R.array.reminder_type_array_res);
        mediator = new TabLayoutMediator(tabs, viewPager2, (tab, position) -> tab.setText(reminderTypeArray[position]));
        mediator.attach();

        Objects.requireNonNull(tabs.getTabAt(0)).select();

        fab = binding.homeFab;
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
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.home_fab) {
            setExitTransition(new Hold());
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(fab, getString(R.string.from_main_to_addUpdate)).build();
            Navigation.findNavController(v).navigate(R.id.addOrUpdateReminderFragment, null, null, extras);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediator.detach();
        mediator = null;
        fab = null;
        binding = null;
    }
}
