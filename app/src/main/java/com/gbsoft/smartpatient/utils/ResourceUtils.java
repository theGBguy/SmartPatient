package com.gbsoft.smartpatient.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.ColorRes;

public class ResourceUtils {
    public static int getColor(Context context, @ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(id, null);
        } else {
            return context.getResources().getColor(id);
        }
    }
}
