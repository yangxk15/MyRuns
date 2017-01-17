package edu.dartmouth.cs.xiankai_yang_myruns.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.dartmouth.cs.xiankai_yang_myruns.R;
import edu.dartmouth.cs.xiankai_yang_myruns.util.FragmentPagerUtil;

/**
 * Created by yangxk15 on 1/16/17.
 */

public class StartFragment extends Fragment implements FragmentPagerUtil {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "START";
    }
}