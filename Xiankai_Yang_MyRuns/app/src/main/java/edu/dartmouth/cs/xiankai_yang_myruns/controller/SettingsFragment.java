package edu.dartmouth.cs.xiankai_yang_myruns.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.dartmouth.cs.xiankai_yang_myruns.R;
import edu.dartmouth.cs.xiankai_yang_myruns.util.FragmentPagerUtil;

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
