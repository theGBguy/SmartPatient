package com.gbsoft.smartpillreminder.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentSettingsBinding;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FragmentSettingsBinding binding;
    private Preference reminderTonePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        SwitchPreferenceCompat switchPreferenceCompat = findPreference(getString(R.string.key_night_mode));
        reminderTonePreference = findPreference(getString(R.string.key_reminder_tone));
        Uri reminderToneUri = Uri.parse(getPreferenceManager().getSharedPreferences().getString(getString(R.string.key_reminder_tone), ""));
        String summaryStr = RingtoneManager.getRingtone(requireContext(), reminderToneUri).getTitle(requireContext());
        reminderTonePreference.setSummary(summaryStr);
        Preference aboutDevPreference = findPreference(getString(R.string.key_about_dev));

        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isChecked = false;
                if (newValue instanceof Boolean)
                    isChecked = (Boolean) newValue;
                if (isChecked) {
                    getPreferenceManager().getSharedPreferences().edit().putBoolean(getString(R.string.key_night_mode), true).apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    getPreferenceManager().getSharedPreferences().edit().putBoolean(getString(R.string.key_night_mode), false).apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });
        reminderTonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent reminderToneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);

                String existingValue = getPreferenceManager().getSharedPreferences().getString(getString(R.string.key_reminder_tone), "");
                if (existingValue != null) {
                    if (existingValue.length() == 0)
                        reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    else
                        reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));

                } else
                    reminderToneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);
                startActivityForResult(reminderToneIntent, 1005);
                return true;
            }
        });

        aboutDevPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(requireContext(), getString(R.string.summary_about_dev), Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            if (requestCode == 1005 && resultCode == Activity.RESULT_OK) {
                Uri reminderTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (reminderTone != null) {
                    getPreferenceManager().getSharedPreferences().edit().putString(getString(R.string.key_reminder_tone), reminderTone.toString()).apply();
                    reminderTonePreference.setSummary(RingtoneManager.getRingtone(requireContext(), reminderTone).getTitle(requireContext()));
                } else {
                    Uri defaultreminderTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    getPreferenceManager().getSharedPreferences().edit().putString(getString(R.string.key_reminder_tone), defaultreminderTone.toString()).apply();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null)
            binding = FragmentSettingsBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = binding.toolbarSettings;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
