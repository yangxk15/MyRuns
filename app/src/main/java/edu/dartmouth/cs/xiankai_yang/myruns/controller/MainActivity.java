package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FragmentPagerUtil;
import lombok.Data;

@Data
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String START = "Start";
    private static final String HISTORY = "History";
    private static final String SETTINGS = "Settings";

    StartFragment mStartFragment;
    HistoryFragment mHistoryFragment;
    SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        final ArrayList<FragmentPagerUtil> fragmentArrayList = new ArrayList<>();

        FragmentManager fm = getFragmentManager();

        if (savedInstanceState == null) {
            mStartFragment = new StartFragment();
            mHistoryFragment = new HistoryFragment();
            mSettingsFragment = new SettingsFragment();
        } else {
            mStartFragment = (StartFragment) fm.findFragmentByTag(
                    getFragmentTag(R.id.main_view_pager, 0)
            );
            mHistoryFragment = (HistoryFragment) fm.findFragmentByTag(
                    getFragmentTag(R.id.main_view_pager, 1)
            );
            mSettingsFragment = (SettingsFragment) fm.findFragmentByTag(
                    getFragmentTag(R.id.main_view_pager, 2)
            );
        }

        fragmentArrayList.add(mStartFragment);
        fragmentArrayList.add(mHistoryFragment);
        fragmentArrayList.add(mSettingsFragment);

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return (Fragment) fragmentArrayList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentArrayList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return fragmentArrayList.get(position).getFragmentPagerTitle();
            }
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(fragmentPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        ArrayList<String> permissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
    }

    private String getFragmentTag(int viewPagerId, int position)
    {
        return "android:switcher:" + viewPagerId + ":" + position;
    }
}
