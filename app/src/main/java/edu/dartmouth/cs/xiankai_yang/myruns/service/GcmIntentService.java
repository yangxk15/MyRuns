package edu.dartmouth.cs.xiankai_yang.myruns.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.dartmouth.cs.xiankai_yang.myruns.controller.MainActivity;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;

/**
 * Created by yangxk15 on 2/22/17.
 */

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {
            if (messageType.equals("gcm")) {
                delete(extras.getString("message"));
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void delete(final String id) {
        ExerciseEntryDbHelper.getInstance(getApplicationContext()).removeEntry(Long.valueOf(id));
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MainActivity.mHistoryFragment.reload();
            }
        });
    }
}