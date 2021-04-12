package com.gbsoft.smartpatient.ui.main.addupdatemed;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentAddUpdateMedicineBinding;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.PermissionHelper;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddUpdateMedicineFrag extends Fragment {
    public static final String KEY_MEDICINE_ID_TO_BE_UPDATED = "key_med_id_to_be_update";
    public static final String KEY_IS_ONLINE = "is_online";
    public static final String KEY_PATIENT_UID = "patient_id";
    public static final String KEY_PATIENT_NAME = "patient_name";

    private AddUpdateViewModel viewModel;
    private FragmentAddUpdateMedicineBinding binding;
    private ActivityResultLauncher<Uri> launcher;

    private void setAppropriateTransition() {
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setDuration(650);
        transform.setScrimColor(getResources().getColor(android.R.color.transparent));
        transform.setPathMotion(new MaterialArcMotion());
        transform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        setSharedElementEnterTransition(transform);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddUpdateMedicineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.addUpdateToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        launcher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result ->
                viewModel.onActivityResult(result));

        viewModel = new ViewModelProvider(this).get(AddUpdateViewModel.class);
        viewModel.init(getArguments(), new ViewModelProvider(requireActivity()).get(MainViewModel.class).getCurrentUser());

        if (viewModel.isNotUpdate())
            setAppropriateTransition();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setEventhandler(new AddUpdateEventHandler());

        viewModel.getSnackMsg().observe(getViewLifecycleOwner(), new EventObserver<>(msg -> {
            if (msg == null || msg == 0) return;
            SnackUtils.showMessageWithCallback(view, msg, new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    transientBottomBar.removeCallback(this);
                    if (msg == R.string.reminderUpdateSuccess_text || msg == R.string.reminderInsertSuccess_text)
                        Navigation.findNavController(view).navigateUp();
                }
            });
        }));

        viewModel.getAddImgEvent().observe(getViewLifecycleOwner(), new EventObserver<>(uri -> {
            if (uri == null) return;
            if (PermissionHelper.checkWritePermission(requireActivity())) {
                launcher.launch(uri);
            } else {
                SnackUtils.showMessage(view, R.string.writePermissionRequired_text);
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        launcher.unregister();
        viewModel = null;
        binding = null;
    }
}
