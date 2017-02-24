package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.backend.registration.Registration;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FragmentPagerUtil;
import lombok.Data;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String START = "Start";
    private static final String HISTORY = "History";
    private static final String SETTINGS = "Settings";

    public static StartFragment mStartFragment;
    public static HistoryFragment mHistoryFragment;
    public static SettingsFragment mSettingsFragment;

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

        new AsyncTask<Context, Void, String>() {
            private Registration regService = null;
            private GoogleCloudMessaging gcm;

            @Override
            protected String doInBackground(Context... contexts) {
                if (regService == null) {
                    regService = new Registration.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null
                    )
                            .setRootUrl(
                                    "https://"
                                    + contexts[0].getString(R.string.project_id)
                                    + ".appspot.com/_ah/api/")
//                            //2/22/17 for local testing
//                            .setRootUrl(contexts[0].getString(R.string.server_address) + "_ah/api/")
//                            .setGoogleClientRequestInitializer(
//                                    new GoogleClientRequestInitializer() {
//                                        @Override
//                                        public void initialize(
//                                                AbstractGoogleClientRequest<?>
//                                                        abstractGoogleClientRequest
//                                        )throws IOException {
//                                            abstractGoogleClientRequest.setDisableGZipContent(true);
//                                        }
//                                    }
//                            )
                            .build();
                }

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(contexts[0]);
                    }

                    String regId = gcm.register(contexts[0].getString(R.string.project_number));
                    msg = "Device registered, registration ID = " + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    regService.register(regId).execute();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute(this);
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
