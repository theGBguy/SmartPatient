package com.gbsoft.smartpatient.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.gbsoft.smartpatient.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PermissionHelper {
    public static boolean checkWritePermission(FragmentActivity activity) {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                new MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setIcon(R.drawable.info)
                        .setTitle("Permission Request!")
                        .setMessage("Please, grant us the permission so that app can work as expected.")
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 102);
            }
        } else
            return true;
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
