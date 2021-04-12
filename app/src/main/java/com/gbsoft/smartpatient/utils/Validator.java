package com.gbsoft.smartpatient.utils;

import android.util.Patterns;

public class Validator {
    // A email/username validation check
    public static boolean isUserNameValid(String username) {
        if (username == null) return false;
        if (username.trim().isEmpty()) return false;
        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    // A password validation check which ensures it's length to be greater than 8
    public static boolean isPasswordValid(String password) {
        if (password == null) return false;
        return password.trim().length() > 8;
    }

    // A phone number validation check which ensures the digits are 10
    public static boolean isPhoneNumValid(String phoneNumber) {
        if (phoneNumber == null) return false;
        return phoneNumber.length() == 10;
    }

    // A gender selection validation check which ensures a gender is selected during registration
    public static boolean isGenderSelected(int radioId) {
        return radioId != -1;
    }

    // A normal text validation check which ensures the text is non-null and non-empty
    public static boolean isTextValid(String text) {
        if (text == null) return false;
        return text.trim().length() > 0;
    }
}
