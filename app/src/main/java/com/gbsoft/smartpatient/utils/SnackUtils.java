package com.gbsoft.smartpatient.utils;

import android.view.View;

import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

public class SnackUtils {
    public static void showMessage(View view, @StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }

    public static void showMessageWithCallback(View view, @StringRes int resId, Snackbar.Callback callback) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).addCallback(callback).show();
    }

    public static void showMessage(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showMessage(View view, @PluralsRes int resId, int quantity, Object... formatArgs) {
        Snackbar.make(view, view.getContext().getResources().getQuantityString(resId, quantity, formatArgs),
                Snackbar.LENGTH_LONG).show();
    }

    public static void showMessage(View view, @PluralsRes int resId, int quantity) {
        Snackbar.make(view, view.getContext().getResources().getQuantityString(resId, quantity),
                Snackbar.LENGTH_LONG).show();
    }
}
