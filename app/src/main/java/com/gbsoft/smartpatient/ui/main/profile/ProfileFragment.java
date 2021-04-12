package com.gbsoft.smartpatient.ui.main.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentProfileBinding;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.newapp.NewAppointmentFrag;
import com.gbsoft.smartpatient.utils.DialogUtils;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    public static final String KEY_UID = "uid";
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    private ActivityResultLauncher<String> getProfileImageLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.profileToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        getProfileImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result == null) return;
            viewModel.setPhotoUrl(result);
        });

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.setVariables(getArguments());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);

        binding.tilAvailability.setEndIconOnClickListener(v -> viewModel.setAvailability());

        AlertDialog picUploadDialog = DialogUtils.getUploadPicDialog(requireContext());
        viewModel.getEvents().observe(getViewLifecycleOwner(), new EventObserver<>(event -> {
            if (event == null) return;
            if (TextUtils.equals(event, ProfileViewModel.EVENT_CALL)) {
                Intent callingIntent = new Intent(Intent.ACTION_DIAL);
                callingIntent.setData(Uri.parse("tel:" + viewModel.getPhoneNum()));
                startActivity(callingIntent);
            } else if (TextUtils.equals(event, ProfileViewModel.EVENT_EMAIL)) {
                Intent emailLauncher = new Intent(Intent.ACTION_SENDTO);
                emailLauncher.setData(Uri.parse("mailto:" + viewModel.getEmail()));
                startActivity(Intent.createChooser(emailLauncher, getResources().getString(R.string.send_email_title)));
            } else if (TextUtils.equals(event, ProfileViewModel.EVENT_ADD_PROFILE_PIC)) {
                getProfileImageLauncher.launch("image/*");
            } else if (TextUtils.equals(event, ProfileViewModel.EVENT_UPLOADING_PIC)) {
                picUploadDialog.show();
            } else if (TextUtils.equals(event, ProfileViewModel.EVENT_UPLOADING_PIC_COMPLETED)) {
                picUploadDialog.dismiss();
            } else {
                Bundle args = new Bundle();
                args.putString(NewAppointmentFrag.KEY_ID, viewModel.getUid());
                args.putString(NewAppointmentFrag.KEY_NAME, viewModel.getUserName.getValue());
                args.putString(NewAppointmentFrag.KEY_AVAILABILITY, viewModel.availability.getValue());
                Navigation.findNavController(view).navigate(R.id.nav_new_appointment, args, null, null);
            }
        }));

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == null || msg == 0) return;
            SnackUtils.showMessage(view, msg);
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }
}