package com.gbsoft.smartpatient.ui.main.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentHealthPersonnelRegisterBinding;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HealthPersonnelRegisterFrag extends Fragment {
    private FragmentHealthPersonnelRegisterBinding binding;
    private HealthPersonnelRegVM viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHealthPersonnelRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HealthPersonnelRegVM.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);

        viewModel.getSnackbarMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == null || msg == 0) return;
            SnackUtils.showMessageWithCallback(view, msg, new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    transientBottomBar.removeCallback(this);
                    if (msg == R.string.registration_ok)
                        Navigation.findNavController(view).navigateUp();
                }
            });
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }
}