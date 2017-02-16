package edu.dartmouth.cs.xiankai_yang.myruns.util;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by yangxk15 on 2/15/17.
 */

public class MessengerHelper {
    private static final String TAG = "MessengerHelper";

    public static void sendMessage(Messenger from, Messenger to, int requestCode) {
        if (to != null) {
            Message message = Message.obtain(null, requestCode);
            message.replyTo = from;
            try {
                to.send(message);
            } catch (RemoteException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
