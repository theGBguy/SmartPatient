package com.gbsoft.smartpatient.ui.main.medicinedetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.databinding.MedicineDetailFragmentBinding;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MedicineDetailFrag extends Fragment {

    public static final String KEY_IS_HP = "is_hp";
    public static final String KEY_MEDICINE = "medicine";

    private MedicineDetailFragmentBinding binding;
    private MedicineDetailViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MedicineDetailFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.medicineDetailToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        viewModel = new ViewModelProvider(this).get(MedicineDetailViewModel.class);
        viewModel.init(requireArguments());
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel.reminders.observe(getViewLifecycleOwner(), reminders -> {
            for (Reminder reminder : reminders) {
                binding.reminderChipGroup.addView(new ReminderChip(requireContext(), reminder));
            }
        });

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), msg -> {
            if (msg == 0) return;
            SnackUtils.showMessageWithCallback(view, msg, new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    transientBottomBar.removeCallback(this);
                    Navigation.findNavController(view).popBackStack();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }
}