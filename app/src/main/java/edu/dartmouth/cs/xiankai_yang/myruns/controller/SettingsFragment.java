package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.os.Bundle;
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
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "SETTINGS";
    }
}
