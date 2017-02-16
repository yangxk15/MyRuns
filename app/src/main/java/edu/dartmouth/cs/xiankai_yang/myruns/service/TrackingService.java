package edu.dartmouth.cs.xiankai_yang.myruns.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.controller.MapDisplayActivity;
import edu.dartmouth.cs.xiankai_yang.myruns.controller.StartFragment;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.WekaClassifier;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.Constants;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FFT;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.MessengerHelper;

/**
 * Created by yangxk15 on 2/7/17.
 */

public class TrackingService extends Service
        implements LocationListener, SensorEventListener {
    private static final String TAG = "TrackingService";
    private static final int NOTIFICATION = 0;
    private static final int BUFFER_SIZE = 64;

    public static final int UPDATE = 0;

    // Only one service instance will be running
    public static ExerciseEntry mExerciseEntry;
    public static boolean mStarted;
    public static float mCurSpeed;
    public static ActivityType mCurrentActivityType;

    private Location lastLocation;
    private Calendar lastCalendar;
    private Location startLocation;

    private Messenger mClientMessenger;
    private final Messenger mMessenger = new Messenger(new MapDisplayActivityHandler());

    private SensorManager mSensorManager;
    private ArrayBlockingQueue<Double> mAccBuffer = new ArrayBlockingQueue<>(BUFFER_SIZE);
    private ActivityTypePredictionTask mPredictionTask;
    public int[] mActivityTypeCounter = new int[3];

    public static boolean mAutomatic;

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

        if (intent.getExtras().getInt(StartFragment.INPUT_TYPE) == InputType.AUTOMATIC.ordinal()) {
            mAutomatic = true;

            mPredictionTask = new ActivityTypePredictionTask();
            mPredictionTask.execute();

            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    mSensorManager.SENSOR_DELAY_FASTEST);
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

        MessengerHelper.sendMessage(mMessenger, mClientMessenger, UPDATE);
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
        mExerciseEntry.setMActivityType(mAutomatic ? -1
                : bundle.getInt(StartFragment.ACTIVITY_TYPE));
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
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NOTIFICATION);

        if (mAutomatic) {
            mSensorManager.unregisterListener(this);
            mPredictionTask.cancel(true);
        }

        mExerciseEntry = null;
        mStarted = false;
    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            try {
                mAccBuffer.add(m);
            } catch (IllegalStateException e) {
                ArrayBlockingQueue<Double> newBuf =
                        new ArrayBlockingQueue<>(mAccBuffer.size() * 2);
                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(m);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private class ActivityTypePredictionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            FFT fft = new FFT(BUFFER_SIZE);

            double[] re = new double[BUFFER_SIZE];

            int i = 0;

            while (true) {
                try {
                    if (isCancelled()) {
                        return null;
                    }

                    re[i++] = mAccBuffer.take();

                    if (i == BUFFER_SIZE) {
                        i = 0;

                        Double[] ma = new Double[BUFFER_SIZE + 1];

                        double max = Double.MIN_VALUE;
                        for (double v : re) {
                            max = Math.max(max, v);
                        }
                        ma[BUFFER_SIZE] = max;

                        double[] im = new double[BUFFER_SIZE];

                        fft.fft(re, im);

                        for (int j = 0; j < re.length; j++) {
                            ma[j] = Math.sqrt(re[j] * re[j] + im[j] * im[j]);
                        }


                        int p = (int) WekaClassifier.classify(ma);
                        mActivityTypeCounter[p]++;
                        switch (p) {
                            case 0:
                                mCurrentActivityType = ActivityType.STANDING;
                                break;
                            case 1:
                                mCurrentActivityType = ActivityType.WALKING;
                                break;
                            case 2:
                                mCurrentActivityType = ActivityType.RUNNING;
                                break;
                            default:
                                Log.d(TAG, "Shouldn't come here!");
                        }

                        int maxIndex = 0;
                        for (int j = 0; j < mActivityTypeCounter.length; j++) {
                            if (mActivityTypeCounter[j] > mActivityTypeCounter[maxIndex]) {
                                maxIndex = j;
                            }
                        }

                        switch (maxIndex) {
                            case 0:
                                mExerciseEntry.setMActivityType(ActivityType.STANDING.ordinal());
                                break;
                            case 1:
                                mExerciseEntry.setMActivityType(ActivityType.WALKING.ordinal());
                                break;
                            case 2:
                                mExerciseEntry.setMActivityType(ActivityType.RUNNING.ordinal());
                                break;
                            default:
                                Log.d(TAG, "Shouldn't come here!");
                        }

                        MessengerHelper.sendMessage(mMessenger, mClientMessenger, UPDATE);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

}
