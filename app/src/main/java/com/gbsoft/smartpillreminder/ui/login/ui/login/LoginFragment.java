package com.gbsoft.smartpillreminder.ui.login.ui.login;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new LoginViewModelFactory().create(LoginViewModel.class);

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            binding.login.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                binding.username.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                binding.password.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            binding.loading.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(binding.username.getText().toString(),
                        binding.password.getText().toString());
            }
        };
        binding.username.addTextChangedListener(afterTextChangedListener);
        binding.password.addTextChangedListener(afterTextChangedListener);
        binding.password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(binding.username.getText().toString(),
                        binding.password.getText().toString());
            }
            return false;
        });

        binding.login.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            loginViewModel.login(binding.username.getText().toString(),
                    binding.password.getText().toString());
        });

        SpannableString spannableStr = new SpannableString(getString(R.string.register_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

            }
        };
        spannableStr.setSpan(clickableSpan, 23, spannableStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStr.setSpan(new ForegroundColorSpan(getColor()), 23, spannableStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.register.setText(spannableStr, TextView.BufferType.SPANNABLE);
        binding.register.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private int getColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(R.color.colorPrimary, requireActivity().getTheme());
        } else {
            return getResources().getColor(R.color.colorPrimary);
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }
}