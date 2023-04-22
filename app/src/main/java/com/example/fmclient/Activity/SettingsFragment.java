package com.example.fmclient.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.fmclient.R;
import com.example.fmclient.Settings;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final Settings settings = Settings.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        SwitchPreferenceCompat lifeEventLineOption = findPreference(getString(R.string.life_event_line_key));
        assert lifeEventLineOption != null;
        lifeEventLineOption.setOnPreferenceClickListener(preference -> {
            settings.setLifeStory(lifeEventLineOption.isChecked());
            return false;
        });

        SwitchPreferenceCompat familyTreeLineOption = findPreference(
                getString(R.string.family_tree_lines_key));
        assert familyTreeLineOption != null;
        familyTreeLineOption.setOnPreferenceClickListener(preference -> {
            settings.setFamilySide(familyTreeLineOption.isChecked());
            return false;
        });

        SwitchPreferenceCompat spouseLineOption = findPreference(
                getString(R.string.spouse_lines_key));
        assert spouseLineOption != null;
        spouseLineOption.setOnPreferenceClickListener(preference -> {
            settings.setSpouseSide(spouseLineOption.isChecked());
            return false;
        });

        SwitchPreferenceCompat fatherSideOption = findPreference(
                getString(R.string.father_side_key));
        assert fatherSideOption != null;
        fatherSideOption.setOnPreferenceClickListener(preference -> {
            settings.setFatherSide(fatherSideOption.isChecked());
            return false;
        });

        SwitchPreferenceCompat motherSideOption = findPreference(
                getString(R.string.mother_side_key));
        assert motherSideOption != null;
        motherSideOption.setOnPreferenceClickListener(preference -> {
            settings.setMotherSide(motherSideOption.isChecked());
            return false;
        });

        SwitchPreferenceCompat maleEventsOption = findPreference(
                getString(R.string.filter_male_key));
        assert maleEventsOption != null;
        maleEventsOption.setOnPreferenceClickListener(preference -> {
            settings.setMaleEvents(maleEventsOption.isChecked());
            return false;
        });

        SwitchPreferenceCompat femaleEventsOption = findPreference(
                getString(R.string.filter_female_key));
        assert femaleEventsOption != null;
        femaleEventsOption.setOnPreferenceClickListener(preference -> {
            settings.setFemaleEvents(femaleEventsOption.isChecked());
            return false;
        });

        Preference logoutButton = findPreference(getString(R.string.logout_key));
        assert logoutButton != null;
        logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Intent intent = new Intent(requireActivity().getApplication(), MainActivity.class);
                intent.putExtra("logout", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
