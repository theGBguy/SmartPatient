package com.gbsoft.smartpatient.ui.main.settings;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentSettingsBinding;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.utils.SnackUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFrag extends PreferenceFragmentCompat {
    private FragmentSettingsBinding binding;
    private ActivityResultLauncher<String> launcher;
    private MutableLiveData<Uri> reminderTone;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        launcher = registerForActivityResult(new PickRingtone(), data -> {
            if (data != null)
                reminderTone.setValue(data);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSettingsBinding.bind(view);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.settingsToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        SwitchPreferenceCompat switchPreferenceCompat = findPreference(getString(R.string.key_night_mode));
        Preference reminderTonePreference = findPreference(getString(R.string.key_reminder_tone));
        Preference aboutDevPreference = findPreference(getString(R.string.key_about_dev));

        if (switchPreferenceCompat != null)
            switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof Boolean) {
                    getPreferenceManager().getSharedPreferences().edit()
                            .putBoolean(getString(R.string.key_night_mode), (Boolean) newValue).apply();
                    AppCompatDelegate.setDefaultNightMode((Boolean) newValue ?
                            AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            });

        Uri reminderToneUri = Uri.parse(getPreferenceManager().getSharedPreferences().getString(getString(R.string.key_reminder_tone),
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));
        String summaryStr = RingtoneManager.getRingtone(requireContext(), reminderToneUri).getTitle(requireContext());

        if (reminderTonePreference != null) {
            reminderTonePreference.setSummary(summaryStr);
            reminderTonePreference.setOnPreferenceClickListener(preference -> {
                launcher.launch(reminderToneUri.toString());
                return true;
            });
        }

        reminderTone = new MutableLiveData<>();
        reminderTone.observe(getViewLifecycleOwner(), uri -> {
            getPreferenceManager().getSharedPreferences().edit().
                    putString(getString(R.string.key_reminder_tone), uri.toString()).apply();
            reminderTonePreference.setSummary(RingtoneManager.getRingtone(requireContext(), uri).getTitle(requireContext()));
        });

        if (aboutDevPreference != null)
            aboutDevPreference.setOnPreferenceClickListener(preference -> {
                SnackUtils.showMessage(view, R.string.summary_about_dev);
                return true;
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        launcher.unregister();
        binding = null;
    }
}
