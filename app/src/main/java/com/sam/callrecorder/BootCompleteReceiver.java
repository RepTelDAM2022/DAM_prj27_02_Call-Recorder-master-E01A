package com.sam.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, CallRecorder.class);
        Toast.makeText(context,"BootCompleteReceiver starting CallRecorder service.",Toast.LENGTH_LONG).show();
        Log.i(TAG, "starting CallRecorder service.");
        context.startService(serviceIntent);
    }

}
