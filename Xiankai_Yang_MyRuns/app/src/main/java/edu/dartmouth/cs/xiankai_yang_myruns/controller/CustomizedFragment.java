package edu.dartmouth.cs.xiankai_yang_myruns.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yangxk15 on 1/16/17.
 */

public abstract class CustomizedFragment extends Fragment{
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    public abstract CharSequence getFragmentPagerTitle();
}
