package com.gbsoft.smartpatient.ui.main.home.offline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentHomeBinding;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.transition.Hold;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OfflineHomeFrag extends Fragment {
    private FragmentHomeBinding binding;
    private TabLayoutMediator mediator;
    private OfflineHomeViewModel viewModel;

    private TabLayoutMediator.TabConfigurationStrategy strategy;
    private AnimatedVectorDrawableCompat[] drawables;

    private final int[] drawablesResArr = {R.drawable.pending_reminder,
            R.drawable.completed_reminder,
            R.drawable.missed_reminder};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.homeToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), navController,
                ((MainActivity) requireActivity()).getAppBarConfig());

        viewModel = new ViewModelProvider(this).get(OfflineHomeViewModel.class);

        binding.setViewmodel(viewModel);
        binding.setEventhandler(new OfflineHomeEventHandler());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.homeViewPager.setAdapter(new TabFragmentStateAdapter(getChildFragmentManager(),
                getViewLifecycleOwner().getLifecycle()));

        mediator = new TabLayoutMediator(new WeakReference<>(binding.homeTabs).get(),
                new WeakReference<>(binding.homeViewPager).get(),
                getStrategy());
        mediator.attach();

        viewModel.getSetExitTransition().observe(getViewLifecycleOwner(), shouldSet ->
                setExitTransition(shouldSet ? new Hold() : null));

        viewModel.getCurrentTabPosLiveData().observe(getViewLifecycleOwner(), pos ->
                getDrawables()[pos].start());

    }

    private TabLayoutMediator.TabConfigurationStrategy getStrategy() {
        if (strategy == null) {
            strategy = (tab, position) -> {
                final String[] reminderTypeArray = getResources().getStringArray(R.array.reminder_type_array_res);
                tab.setText(reminderTypeArray[position]);
                tab.setIcon(getDrawables()[position]);
                getDrawables()[position].start();
                if (position == 0) tab.select();
            };
        }
        return strategy;
    }

    public AnimatedVectorDrawableCompat[] getDrawables() {
        if (drawables == null) {
            drawables = new AnimatedVectorDrawableCompat[3];
            for (int i = 0; i < 3; i++) {
                drawables[i] = AnimatedVectorDrawableCompat.create(requireContext(), drawablesResArr[i]);
            }
        }
        return drawables;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediator.isAttached())
            mediator.detach();
        viewModel = null;
        mediator = null;
        drawables = null;
        binding = null;
        strategy = null;
    }
}
