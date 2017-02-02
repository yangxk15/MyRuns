package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FragmentPagerUtil;

/**
 * Created by yangxk15 on 1/16/17.
 */

public class SettingsFragment extends PreferenceFragment implements FragmentPagerUtil {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);

        ListPreference listPreference =
                (ListPreference) findPreference(getString(R.string.settings_unit_preference));
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ((MainActivity) getActivity()).getMHistoryFragment().reload();
                return true;
            }
        });
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "SETTINGS";
    }
}
