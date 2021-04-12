package com.gbsoft.smartpatient.ui.main.appointmentlist;

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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.databinding.FragmentAppointmentListBinding;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.gbsoft.smartpatient.utils.CustomMapper;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppointmentListFrag extends Fragment {
    private FragmentAppointmentListBinding binding;
    private AppointmentListVM viewModel;

    private RVEmptyObserver usrObv;
    private RVEmptyObserver appObv;

    private PatientListAdapter pAdapter;
    private HealthPersonnelListAdapter hpAdapter;
    private AppointmentListAdapter appAdapter;

    private ClickObserver observer;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppointmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.appListToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        viewModel = new ViewModelProvider(this).get(AppointmentListVM.class);
        new ViewModelProvider(requireActivity()).get(MainViewModel.class)
                .isCurrentUserAPatient.observe(getViewLifecycleOwner(), isPatient -> {
            if (isPatient == null) return;
            viewModel.setIsHealthPersonnel(!isPatient);
            setUserRecyclerView(!isPatient);
            setAppointmentRecyclerView(!isPatient);
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == null || msg == 0) return;
            SnackUtils.showMessage(view, msg);
        }));
    }

    private void setUserRecyclerView(boolean isHp) {
        usrObv = new RVEmptyObserver(binding.rvUsrList, binding.tvNoUsr);
        if (isHp) {
            viewModel.getPatientQuery().observe(getViewLifecycleOwner(), query -> {
                if (query == null) return;
                FirestoreRecyclerOptions<Patient> usrOptions = new FirestoreRecyclerOptions.Builder<Patient>()
                        .setQuery(query, Patient.class)
                        .setLifecycleOwner(getViewLifecycleOwner())
                        .build();

                pAdapter = new PatientListAdapter(usrOptions, usrObv, PatientVM.ON_CLICK_TO_PROFILE);
                pAdapter.registerAdapterDataObserver(usrObv);
                binding.rvUsrList.setAdapter(pAdapter);
            });

        } else {
            FirestoreRecyclerOptions<HealthPersonnel> usrOptions = new FirestoreRecyclerOptions.Builder<HealthPersonnel>()
                    .setQuery(viewModel.getHealthPersonnelQuery(), HealthPersonnel.class)
                    .setLifecycleOwner(getViewLifecycleOwner())
                    .build();

            hpAdapter = new HealthPersonnelListAdapter(usrOptions, usrObv, PatientVM.ON_CLICK_TO_PROFILE);
            hpAdapter.registerAdapterDataObserver(usrObv);
            binding.rvUsrList.setAdapter(hpAdapter);
        }
        binding.rvUsrList.setHasFixedSize(false);
    }

    private void setAppointmentRecyclerView(boolean isHp) {
        FirestoreRecyclerOptions<Appointment> appOptions = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(isHp ? viewModel.getHpSpecificAppQuery() : viewModel.getPatientSpecificAppQuery(), CustomMapper::getAppointmentFromSnapshot)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        appObv = new RVEmptyObserver(binding.rvAppointmentList, binding.tvNoApp);
        appAdapter = new AppointmentListAdapter(appOptions, isHp, appObv, getObserver());
        appAdapter.registerAdapterDataObserver(appObv);
        binding.rvAppointmentList.setHasFixedSize(true);
        binding.rvAppointmentList.setAdapter(appAdapter);
    }

    private ClickObserver getObserver() {
        if (observer == null) {
            observer = (appId, isApproved) -> viewModel.setApprovalStatus(appId, isApproved);
        }
        return observer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (hpAdapter != null) hpAdapter.unregisterAdapterDataObserver(usrObv);
        if (appAdapter != null) appAdapter.unregisterAdapterDataObserver(appObv);
        observer = null;
        binding = null;
        viewModel = null;
    }
}