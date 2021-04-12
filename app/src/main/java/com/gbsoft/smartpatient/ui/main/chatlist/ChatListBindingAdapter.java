package com.gbsoft.smartpatient.ui.main.chatlist;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListBindingAdapter {
    @BindingAdapter("hideIfTrue")
    public static void hideViewIfTrue(View view, boolean param) {
        if (view instanceof FloatingActionButton) {
            if (param)
                ((FloatingActionButton) view).hide();
            else
                ((FloatingActionButton) view).show();
        } else {
            view.setVisibility(param ? View.GONE : View.VISIBLE);
        }

    }
}
