package com.gbsoft.smartpatient.ui.main.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentLoginBinding;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.SnackUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFrag extends Fragment {
    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(loginViewModel);

        loginViewModel.getNextFragment().observe(getViewLifecycleOwner(), new EventObserver<>(nextFragment -> {
            if (TextUtils.equals(nextFragment, LoginViewModel.OFFLINE_HOME))
                Navigation.findNavController(view).navigate(R.id.nav_offline_home);
            else if (TextUtils.equals(nextFragment, LoginViewModel.ONLINE_HOME))
                Navigation.findNavController(view).navigate(R.id.nav_online_home);
            else
                Navigation.findNavController(view).navigate(R.id.nav_settings);
        }));

        loginViewModel.getSnackbarMsg().observe(getViewLifecycleOwner(), new EventObserver<>(data -> {
            SnackUtils.showMessage(view, data);
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        loginViewModel = null;
    }
}