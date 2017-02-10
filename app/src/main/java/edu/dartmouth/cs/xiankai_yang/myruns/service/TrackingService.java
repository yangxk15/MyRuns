package edu.dartmouth.cs.xiankai_yang.myruns.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.controller.MapDisplayActivity;
import edu.dartmouth.cs.xiankai_yang.myruns.controller.StartFragment;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.util.Constants;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yangxk15 on 2/7/17.
 */

public class TrackingService extends Service implements LocationListener {
    private static final String TAG = "TrackingService";
    private static final int NOTIFICATION = 0;

    public static final int UPDATE = 0;

    // Only one service instance will be running
    public static ExerciseEntry mExerciseEntry;
    public static boolean mStarted;
    public static float mCurSpeed;

    private Location lastLocation;
    private Calendar lastCalendar;
    private Location startLocation;

    private Messenger mClientMessenger;
    private final Messenger mMessenger = new Messenger(new MapDisplayActivityHandler());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        setupNotification(intent.getExtras());

        initExerciseEntry(intent.getExtras());

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(provider, 500, 1, this);
            lastLocation = startLocation = locationManager.getLastKnownLocation(provider);
            mExerciseEntry.getMLocationList().add(
                    new LatLng(startLocation.getLatitude(), startLocation.getLongitude())
            );
        }

        mStarted = true;
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!mStarted) {
            return;
        }
        Log.d(TAG, "onLocationChanged");

        updateExerciseEntry(location);

        if (mClientMessenger != null) {
            Message message = Message.obtain(null, UPDATE);
            message.replyTo = mMessenger;

            try {
                mClientMessenger.send(message);
            } catch (RemoteException e) {
                mClientMessenger = null;
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        cleanUp();
    }

    @TargetApi(16)
    private void setupNotification(Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), MapDisplayActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getBaseContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new Notification.Builder(this)
                .setContentTitle("My Runs")
                .setContentText("Recording your path now")
                .setSmallIcon(R.drawable.notify)
                .setContentIntent(pendingIntent).build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(NOTIFICATION, notification);
    }

    private void initExerciseEntry(Bundle bundle) {
        mExerciseEntry = new ExerciseEntry();
        mExerciseEntry.setMInputType(bundle.getInt(StartFragment.INPUT_TYPE));
        mExerciseEntry.setMActivityType(bundle.getInt(StartFragment.ACTIVITY_TYPE));
        mExerciseEntry.setMDateTime(lastCalendar = Calendar.getInstance());
        mExerciseEntry.setMLocationList(new ArrayList<LatLng>());
    }

    private void updateExerciseEntry(Location location) {
        Calendar calendar = Calendar.getInstance();

        mExerciseEntry.getMLocationList().add(
                new LatLng(location.getLatitude(), location.getLongitude())
        );
        mExerciseEntry.setMDuration(
                (int) Math.ceil(
                        (double)
                        (calendar.getTimeInMillis()
                                - mExerciseEntry.getMDateTime().getTimeInMillis()) / 1000
                )
        );
        mCurSpeed = (float) (
                location.distanceTo(lastLocation) * Constants.MILES_PER_KILOMETER
                        / (calendar.getTimeInMillis() - lastCalendar.getTimeInMillis()) * 3600
        );
        mExerciseEntry.setMDistance(
                mExerciseEntry.getMDistance() +
                        (float) (location.distanceTo(lastLocation)
                                / 1000 * Constants.MILES_PER_KILOMETER)
        );
        mExerciseEntry.setMAvgSpeed(
                mExerciseEntry.getMDistance() / mExerciseEntry.getMDuration() * 3600
        );
        mExerciseEntry.setMCalorie(
                (int) (mExerciseEntry.getMDistance() * 200)
        );
        mExerciseEntry.setMClimb(
                (float) ((location.getAltitude() - startLocation.getAltitude())
                        / 1000 * Constants.MILES_PER_KILOMETER)
        );

        lastLocation = location;
        lastCalendar = calendar;
    }

    private class MapDisplayActivityHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MapDisplayActivity.REGISTER:
                    mClientMessenger = message.replyTo;
                    break;
                case MapDisplayActivity.UNREGISTER:
                    mClientMessenger = null;
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    public void onTaskRemoved(Intent rootIntent) {
        cleanUp();
        stopSelf();
    }

    private void cleanUp() {
        mExerciseEntry = null;
        mStarted = false;
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NOTIFICATION);
    }

}
