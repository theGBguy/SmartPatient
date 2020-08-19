package com.gbsoft.smartpillreminder.ui.addorupdate;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentAddUpdateReminderBinding;
import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;
import com.gbsoft.smartpillreminder.ui.MainActivity;
import com.gbsoft.smartpillreminder.utils.Helper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class AddOrUpdateReminderFragment extends Fragment implements View.OnClickListener {
    public static final String KEY_REMINDER_TO_BE_UPDATED = "keyReminderToBeUpdated";
    private String newPhotoPath = "", oldPhotoPath = "", medicineType = "", reminderTime = "";

    private static final int IMG_CAPTURE_REQUEST_CODE = 1000;

    private Reminder reminderToBeUpdated;
    private ReminderViewModel reminderViewModel;

    private CustomEditText edtTxtMedName, edtTxtDailyIntake;
    private MaterialButton btnCaptureMedPic, btnSetReminderTime;

    private FragmentAddUpdateReminderBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            reminderToBeUpdated = getArguments().getParcelable(KEY_REMINDER_TO_BE_UPDATED);
        }
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        int color = 0;
        TypedValue a = new TypedValue();
        requireActivity().getTheme().resolveAttribute(android.R.attr.colorBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            color = a.data;
        }

        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setDuration(650);
        transform.setContainerColor(color != 0 ? color : getResources().getColor(R.color.lightWhite));
        transform.setScrimColor(getResources().getColor(android.R.color.transparent));
        transform.setPathMotion(new MaterialArcMotion());
        transform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        setSharedElementEnterTransition(transform);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddUpdateReminderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private boolean isAllViewChecked(View[] views) {
        int checked = 1;
        for (View view : views) {
            if (view instanceof CustomEditText) {
                if (((CustomEditText) view).getDrawableID() == R.drawable.check)
                    checked *= 1;
                else
                    checked *= 0;
            }
        }
        return checked == 1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMG_CAPTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (!oldPhotoPath.equals("")) {
                    Timber.d("old photo is being deleted");
                    if (new File(oldPhotoPath).delete())
                        Timber.d("old photo is cleared.");
                    oldPhotoPath = "";
                }
                Snackbar.make(requireView(), "Image of Reminder is stored successfully", Snackbar.LENGTH_LONG).show();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(new File(newPhotoPath)));
                requireContext().sendBroadcast(mediaScanIntent);
                btnCaptureMedPic.setText(new File(newPhotoPath).getName());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (!newPhotoPath.equals("")) {
                    Timber.d("new photo is being deleted");
                    if (new File(newPhotoPath).delete())
                        Timber.d("new photo is cleared.");
                    newPhotoPath = "";
                }
            }
        }
    }

    private Uri createImageFile(String ReminderName) {
        String uniqueTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imgFileName = ReminderName + "_" + uniqueTimeStamp + "_";
        File storageDir = Objects.requireNonNull(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imgFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        newPhotoPath = Objects.requireNonNull(imageFile).getAbsolutePath();
        return FileProvider.getUriForFile(requireContext(), "com.gbsoft.fileprovider", Objects.requireNonNull(imageFile));
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final MaterialToolbar toolbar = binding.addUpdateToolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(binding.addUpdateToolbar, Navigation.findNavController(requireView()),
                ((MainActivity) requireActivity()).getAppBarConfig());

        edtTxtMedName = binding.etMedName;
        edtTxtDailyIntake = binding.etDailyIntake;
        btnCaptureMedPic = binding.btnAddMedImg;

        btnCaptureMedPic.setOnClickListener(this);
        final MaterialButton btnAddOrUpdate = binding.btnAddUpdate;
        btnAddOrUpdate.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

        final Spinner spinnerMedType = binding.spnrMedType;

        spinnerMedType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medicineType = getResources().getStringArray(R.array.spinner_med_type_array_res)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "Please select the Reminder type from the list", Toast.LENGTH_LONG).show();
            }
        });
        btnSetReminderTime = binding.btnSetReminderTime;
        btnSetReminderTime.setOnClickListener(this);

        if (reminderToBeUpdated != null) {
            toolbar.setTitle(R.string.update_reminder_fragment_label);
            toolbar.setSubtitle("\"" + reminderToBeUpdated.getMedicineName() + "\"");
            edtTxtMedName.setText(reminderToBeUpdated.getMedicineName());

            medicineType = reminderToBeUpdated.getMedicineType();
            spinnerMedType.setSelection(((ArrayAdapter<String>) spinnerMedType.getAdapter()).getPosition(medicineType));

            reminderTime = reminderToBeUpdated.getReminderTime();
            btnSetReminderTime.setText(new Helper.TimeHelper().formatTime(reminderTime));

            edtTxtDailyIntake.setText(String.valueOf(reminderToBeUpdated.getDailyIntake()));
            oldPhotoPath = reminderToBeUpdated.getImagePath();
            btnCaptureMedPic.setText(new File(oldPhotoPath).getName());
            btnAddOrUpdate.setText(R.string.fragment_update_button_text);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_med_img:
                if (checkWritePermission()) {
                    Intent imgCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imgCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile(edtTxtMedName.getText().toString().trim()));
                    if (imgCaptureIntent.resolveActivity(requireActivity().getPackageManager()) != null)
                        startActivityForResult(imgCaptureIntent, IMG_CAPTURE_REQUEST_CODE);
                } else {
                    Snackbar.make(requireView(), "Your permission is required to capture and store any image.", BaseTransientBottomBar.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_set_reminder_time:
                if (reminderTime.length() == 0) {
                    final TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                        reminderTime = hourOfDay + ":" + minute;
                        btnSetReminderTime.setText(new Helper.TimeHelper().formatTime(reminderTime));
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                    dialog.show();
                } else {
                    int[] hrMin = new Helper.TimeHelper().getRemHrMin(reminderTime);
                    TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                        reminderTime = hourOfDay + ":" + minute;
                        btnSetReminderTime.setText(new Helper.TimeHelper().formatTime(reminderTime));
                    }, hrMin[0], hrMin[1], true);
                    dialog.show();
                }
                break;
            case R.id.btn_cancel:
                if (!newPhotoPath.equals("")) {
                    if (new File(newPhotoPath).delete())
                        Timber.d("new photo is cleared.");
                }
                Navigation.findNavController(requireView()).navigateUp();
                break;
            case R.id.btn_add_update:
                if (!isAllViewChecked(new View[]{edtTxtMedName, edtTxtDailyIntake}) || medicineType.equals(getResources().getStringArray(R.array.spinner_med_type_array_res)[0]) || reminderTime.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill out every information properly!", Toast.LENGTH_LONG).show();
                } else {
                    Helper.ReminderHelper reminderHelper = new Helper.ReminderHelper(requireContext());
                    String reminderName = edtTxtMedName.getText().toString().trim();
                    if (reminderTime.length() == 0)
                        reminderTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
                    final Reminder newReminder = new Reminder(System.currentTimeMillis(),
                            reminderName,
                            medicineType,
                            reminderTime,
                            Integer.parseInt(edtTxtDailyIntake.getText().toString().trim()),
                            newPhotoPath.equals("") ? oldPhotoPath : newPhotoPath,
                            "Pending",
                            "f");// f means first
                    String snackbarMsg;
                    if (reminderToBeUpdated != null && reminderToBeUpdated.getReminderType().equals("Pending")) {
                        newReminder.setId(reminderToBeUpdated.getId());
                        reminderViewModel.updateAReminder(newReminder);
                        reminderHelper.scheduleReminder(newReminder, true);
                        snackbarMsg = "The current reminder has been updated successfully!";

                    } else {
                        if (reminderToBeUpdated != null) {
                            newReminder.setMedicineNotes("s");// s means second
                        }
                        reminderViewModel.insertAReminder(newReminder);
                        reminderHelper.scheduleReminder(newReminder, false);
                        snackbarMsg = "A new reminder has been scheduled successfully!";
                    }

                    Snackbar snackbar = Snackbar.make(requireView(), snackbarMsg, Snackbar.LENGTH_LONG);
                    snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            newPhotoPath = "";
                            Navigation.findNavController(requireView()).navigateUp();
                        }
                    });
                    snackbar.show();
                }
                break;
        }
    }

    private boolean checkWritePermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(requireActivity().getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                builder.setIcon(R.drawable.info);
                builder.setTitle("Permission Request!");
                builder.setMessage("Please, grant us the permission so that app can work as expected.");
                builder.show();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, 102);
            }
        } else
            return true;
        return ContextCompat.checkSelfPermission(requireActivity().getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        edtTxtDailyIntake = null;
        edtTxtMedName = null;
        btnCaptureMedPic = null;
        btnSetReminderTime = null;
        reminderViewModel = null;
        binding = null;
    }
}
