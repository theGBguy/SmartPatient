package com.gbsoft.smartpatient.ui.main.newapp;

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

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentNewAppointmentBinding;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewAppointmentFrag extends Fragment {
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_AVAILABILITY = "availability";

    private FragmentNewAppointmentBinding binding;
    private NewAppointmentViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.newAppToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        viewModel = new ViewModelProvider(this).get(NewAppointmentViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);
        binding.setEventhandler(new NewAppointmentEventHandler());
        binding.setFragmentmanager(requireActivity().getSupportFragmentManager());

        viewModel.setVariables(getArguments(), new ViewModelProvider(requireActivity()).get(MainViewModel.class));

        viewModel.shouldNavigateBack().observe(getViewLifecycleOwner(), new EventObserver<>(shouldNavigate -> {
            if (shouldNavigate)
                Navigation.findNavController(view).navigateUp();
        }));

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == null || msg == 0)
                return;
            SnackUtils.showMessageWithCallback(view, msg, new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    transientBottomBar.removeCallback(this);
                    if (msg == R.string.insertAppointmentSuccess_txt)
                        Navigation.findNavController(view).navigateUp();
                }
            });
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel = null;
        binding = null;
    }
}