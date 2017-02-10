package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;
import edu.dartmouth.cs.xiankai_yang.myruns.service.TrackingService;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.Constants;

@TargetApi(24)
public class MapDisplayActivity extends AppCompatActivity
        implements OnMapReadyCallback, ServiceConnection {
    private static final String TAG = "MapDisplayActivity";

    public static final int REGISTER = 0;
    public static final int UNREGISTER = 1;

    boolean mConnected = false;
    boolean mHistory = false;

    TrackingService mTrackingService;
    ExerciseEntry mExerciseEntry;

    private Messenger mServiceMessenger;
    private Messenger mMessenger = new Messenger(new TrackingServiceHandler());

    private GoogleMap mGoogleMap;

    private Polyline mPolyline;
    private Marker startLocation;
    private Marker endLocation;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);

        mTextView = (TextView) findViewById(R.id.map_stats);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map))
                .getMapAsync(this);

        if (getIntent().getExtras().getString(HistoryFragment.EXERCISE_ENTRY) != null) {
            mHistory = true;
            mExerciseEntry = new Gson().fromJson(
                    getIntent().getExtras().getString(HistoryFragment.EXERCISE_ENTRY),
                    ExerciseEntry.class
            );
            ((Button) findViewById(R.id.map_save)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.map_cancel)).setVisibility(View.GONE);
        }

        if (!mHistory && !TrackingService.mStarted) {
            Intent intent = new Intent(this, TrackingService.class);
            intent.putExtras(getIntent().getExtras());
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        super.onStart();

        doBindService();
    }

    @Override
    protected void onStop() {
        super.onStop();

        doUnbindService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (mHistory) {
            display();
        }
    }

    protected void display() {
        if (mExerciseEntry == null) {
            return;
        }
        Log.d(TAG, "display");

        // Set up a bound to include all locations
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Draw the markers
        if (startLocation != null) {
            startLocation.remove();
        }
        if (endLocation != null) {
            endLocation.remove();
        }
        startLocation = mGoogleMap.addMarker(new MarkerOptions()
                .position(mExerciseEntry.getMLocationList().get(0))
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)
                )
        );
        startLocation.setZIndex(0);

        endLocation = mGoogleMap.addMarker(new MarkerOptions()
                .position(mExerciseEntry.getMLocationList().get(
                        mExerciseEntry.getMLocationList().size() - 1
                ))
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)
                )
        );

        // Draw the polyline
        if (mPolyline != null) {
            mPolyline.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        for (LatLng latLng : mExerciseEntry.getMLocationList()) {
            polylineOptions.add(latLng);
            builder.include(latLng);
        }
        mPolyline = mGoogleMap.addPolyline(polylineOptions);
        setStats();

        // Animate the camera
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                builder.build(),
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels,
                getResources().getDisplayMetrics().widthPixels / 10));

    }

    protected void update() {
        if (mExerciseEntry == null) {
            return;
        }
        Log.d(TAG, "update");
        endLocation.remove();
        endLocation = mGoogleMap.addMarker(new MarkerOptions()
                .position(mExerciseEntry.getMLocationList().get(
                        mExerciseEntry.getMLocationList().size() - 1
                ))
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)
                )
        );
        mPolyline.remove();
        PolylineOptions polylineOptions = new PolylineOptions();
        for (LatLng latLng : mExerciseEntry.getMLocationList()) {
            polylineOptions.add(latLng);
        }
        mPolyline = mGoogleMap.addPolyline(polylineOptions);
        setStats();
    }

    @TargetApi(24)
    private void setStats() {
        mTextView.setText(TextUtils.join(System.lineSeparator(), new String[]{
                "Type: " + ActivityType.values()[mExerciseEntry.getMActivityType()].toString(),
                "Avg speed: " + ExerciseEntryAdapter.getDistanceByUnitPreference(
                        mExerciseEntry.getMAvgSpeed(), this
                ) + "/h",
                "Cur speed: " +
                        (mHistory ? "n/a" : (ExerciseEntryAdapter.getDistanceByUnitPreference(
                                TrackingService.mCurSpeed, this
                        ) + "/h")),
                "Climb: " + ExerciseEntryAdapter.getDistanceByUnitPreference(
                        mExerciseEntry.getMClimb(), this
                ),
                "Calorie: " + mExerciseEntry.getMCalorie() + " cals",
                "Distance: " + ExerciseEntryAdapter.getDistanceByUnitPreference(
                        mExerciseEntry.getMDistance(), this
                )
        }));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");

        mServiceMessenger = new Messenger(service);

        Message message = Message.obtain(null, REGISTER);
        message.replyTo = mMessenger;

        try {
            mServiceMessenger.send(message);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }

        mExerciseEntry = TrackingService.mExerciseEntry;
        display();
    }

    // Service unexpectedly crashes
    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceMessenger = null;
        Log.d(TAG, "onServiceDisconnected");
    }

    public void onClickSaveMap(View view) {
        long id = ExerciseEntryDbHelper.getInstance(this).insertEntry(mExerciseEntry);
        Toast.makeText(getApplicationContext(),
                "Entry #" + id + " Saved", Toast.LENGTH_SHORT).show();
        doUnbindService();
        stopService(new Intent(this, TrackingService.class));
        setResult(RESULT_OK, new Intent());
        finish();
    }

    public void onClickCancelMap(View view) {
        Toast.makeText(getApplicationContext(),
                "Entry Discarded", Toast.LENGTH_SHORT).show();
        doUnbindService();
        stopService(new Intent(this, TrackingService.class));
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    public void doBindService() {
        if (!mHistory && !mConnected) {
            bindService(new Intent(this, TrackingService.class), this, Context.BIND_AUTO_CREATE);
            mConnected = true;
        }
    }

    public void doUnbindService() {
        if (!mHistory && mConnected) {
            if (mServiceMessenger != null) {
                Message message = Message.obtain(null, UNREGISTER);
                message.replyTo = mMessenger;

                try {
                    mServiceMessenger.send(message);
                } catch (RemoteException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
            unbindService(this);
            mConnected = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_display_delete:
                ExerciseEntryDbHelper.getInstance(this).removeEntry(mExerciseEntry.getId());
                setResult(RESULT_OK, new Intent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getExtras().getString(HistoryFragment.EXERCISE_ENTRY) != null) {
            getMenuInflater().inflate(R.menu.menu_map_display, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private class TrackingServiceHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.what == TrackingService.UPDATE) {
                update();
            } else {
                super.handleMessage(message);
            }
        }
    }

}
