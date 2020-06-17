package com.gbsoft.smartpillreminder.ui.addorupdate;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.gbsoft.smartpillreminder.R;
import com.google.android.material.textfield.TextInputEditText;

public class CustomEditText extends TextInputEditText implements TextWatcher {
    private int drawableId = -1;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (getInputType() - 1 == InputType.TYPE_TEXT_VARIATION_PERSON_NAME) {
            if (s.length() > 2) {
                drawableId = R.drawable.check;
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.check, 0);
            } else {
                drawableId = R.drawable.wrong;
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.wrong, 0);
            }
        } else if (getInputType() == InputType.TYPE_CLASS_NUMBER) {
            if (s.length() > 0) {
                drawableId = R.drawable.check;
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.check, 0);
            } else {
                drawableId = R.drawable.wrong;
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.wrong, 0);
            }
        }
    }

    public int getDrawableID() {
        return drawableId;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
