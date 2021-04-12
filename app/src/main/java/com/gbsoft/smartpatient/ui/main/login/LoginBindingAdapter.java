package com.gbsoft.smartpatient.ui.main.login;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LoginBindingAdapter {

    @BindingAdapter("hideIfLoggingIn")
    public static void hideIfLoggingIn(View view, boolean isLoggingIn) {
        if (view instanceof MaterialButton)
            view.setVisibility(isLoggingIn ? View.INVISIBLE : View.VISIBLE);
        else
            view.setVisibility(isLoggingIn ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter("errorText")
    public static void showErrorText(TextInputLayout view, LiveData<Integer> errorResLive) {
        Integer errorRes = errorResLive.getValue();
        view.setError(null);
        if (view.getEditText().isFocused()) {
            if (errorRes == null || errorRes == -1)
                return;
            view.setError(view.getContext().getString(errorRes));
        }
    }

}
