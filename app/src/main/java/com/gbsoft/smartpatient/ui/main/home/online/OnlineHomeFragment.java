package com.gbsoft.smartpatient.ui.main.home.online;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.databinding.FragmentOnlineHomeBinding;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.gbsoft.smartpatient.ui.main.appointmentlist.PatientListAdapter;
import com.gbsoft.smartpatient.ui.main.appointmentlist.PatientVM;
import com.gbsoft.smartpatient.utils.CustomMapper;
import com.gbsoft.smartpatient.utils.DialogUtils;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class OnlineHomeFragment extends Fragment {
    private FragmentOnlineHomeBinding binding;
    private OnlineHomeViewModel viewModel;

    private RVEmptyObserver patientObv;
    private RVEmptyObserver medObv;

    private PatientListAdapter pAdapter;
    private MedicineListAdapter mAdapter;

    private ChangeEventListener listener;
    private String queryString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnlineHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.homeToolbarOnline);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), navController,
                ((MainActivity) requireActivity()).getAppBarConfig());

        viewModel = new ViewModelProvider(this).get(OnlineHomeViewModel.class);
        viewModel.init();
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);

        viewModel.shouldNavigateToLogin().observe(getViewLifecycleOwner(), new EventObserver<>(shouldLaunch -> {
            if (shouldLaunch)
                navController.navigate(R.id.nav_login);
            else {
                MainViewModel mainVm = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
                mainVm.initializeCurrentUser();
                mainVm.isCurrentUserAPatient.observe(getViewLifecycleOwner(), isPatient -> {
                    if (isPatient == null) return;
                    User current = mainVm.getCurrentUser();
                    queryString = current.getId() + "_" + current.getName();
                    viewModel.setIsHp(!isPatient);
                    setPatientRecyclerView(!isPatient);
                    setMedicinesRecyclerView(!isPatient);
                });
            }
        }));
    }

    private void setPatientRecyclerView(boolean isHp) {
        if (isHp) {
            viewModel.getPatientQuery().observe(getViewLifecycleOwner(), query -> {
                if (query == null) return;
                FirestoreRecyclerOptions<Patient> usrOptions = new FirestoreRecyclerOptions.Builder<Patient>()
                        .setQuery(query, Patient.class)
                        .setLifecycleOwner(getViewLifecycleOwner())
                        .build();

                patientObv = new RVEmptyObserver(binding.rvPatientList, binding.tvNoUsr);
                pAdapter = new PatientListAdapter(usrOptions, patientObv, PatientVM.ON_CLICK_TO_ADD_UPDATE);
                pAdapter.registerAdapterDataObserver(patientObv);
                binding.rvPatientList.setAdapter(pAdapter);
                binding.rvPatientList.setHasFixedSize(false);
            });
        }
    }

    private void setMedicinesRecyclerView(boolean isHp) {
        FirestoreRecyclerOptions<Medicine> medOptions = new FirestoreRecyclerOptions.Builder<Medicine>()
                .setQuery(isHp ? viewModel.getHPMedicinesQuery(queryString) : viewModel.getPatientMedicinesQuery(queryString),
                        CustomMapper::getMedicineFromSnapshot)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();
        medObv = new RVEmptyObserver(binding.rvMedicinesList, binding.tvNoMedicines);
        mAdapter = new MedicineListAdapter(medOptions, isHp, medObv);
        mAdapter.registerAdapterDataObserver(medObv);
        if (!isHp)
            mAdapter.getSnapshots().addChangeEventListener(getListener());
        binding.rvMedicinesList.setHasFixedSize(false);
        binding.rvMedicinesList.setAdapter(mAdapter);
    }

    private ChangeEventListener getListener() {
        if (listener == null) {
            listener = new ChangeEventListener() {
                @Override
                public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                    if (type == ChangeEventType.ADDED) {
                        viewModel.schedulePendingReminders(CustomMapper.getMedicineFromSnapshot(snapshot));
                    } else if (type == ChangeEventType.REMOVED)
                        viewModel.cancelAllPendingReminders(CustomMapper.getMedicineFromSnapshot(snapshot));
                }

                @Override
                public void onDataChanged() {

                }

                @Override
                public void onError(@NonNull FirebaseFirestoreException e) {
                    Timber.d(e.getLocalizedMessage());
                }
            };
        }
        return listener;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            DialogUtils.showLogoutDialog(requireContext(), Navigation.findNavController(requireView()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pAdapter != null) pAdapter.unregisterAdapterDataObserver(patientObv);
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(medObv);
            if (listener != null) {
                mAdapter.getSnapshots().removeChangeEventListener(listener);
                listener = null;
            }
        }
        binding = null;
        viewModel = null;
    }
}