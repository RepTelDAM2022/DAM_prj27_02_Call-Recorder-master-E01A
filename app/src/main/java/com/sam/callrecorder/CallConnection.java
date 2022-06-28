package com.sam.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Connection;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallConnection extends Connection{
    private static String TAG = "CallConnection";

    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public CallConnection(Context con){
    //public CallConnection(){
        context = con;
        setConnectionProperties(PROPERTY_SELF_MANAGED);
        //setAudioModeIsVoip(true); //???
    }

    @Override
    public void onAnswer(){
        Log.d(TAG, "onAnswer() called");
        //Accept the Call
    }

    @Override
    public void onShowIncomingCallUi() {
        Log.i("Call","Incoming Call");
        super.onShowIncomingCallUi();

        //        MainActivity con = new MainActivity();
        //        Context context = con.getApplicationContext();

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("channel", "Incoming Calls",
                    NotificationManager.IMPORTANCE_HIGH);
        }
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        // other channel setup stuff goes here.

        // We'll use the default system ringtone for our incoming call notification channel.  You can
        // use your own audio resource here.
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setSound(ringtoneUri, new AudioAttributes.Builder()
                    // Setting the AudioAttributes is important as it identifies the purpose of your
                    // notification sound.
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
        }

        //        NotificationManager mgr = context.getSystemService(NotificationManager.class);
        //        mgr.createNotificationChannel(channel);


        // Create an intent which triggers your fullscreen incoming call user interface.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(context, IncomingCallScreenActivity.class);
        intent.setClass(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);

        Log.i("Intent1","" + intent);
        Log.i("Intent2","" + intent.getPackage());
        Log.i("Intent3","" + intent.getType());
        Log.i("Intent4","" + intent.getData());
        Log.i("Intent5","" + intent.getDataString());
        Log.i("Intent6","" + intent.getAction());
        Log.i("Intent7","" + intent.getCategories());
        Log.i("Intent8","" + intent.getExtras());

        Log.i("Pending Intent","" + pendingIntent);
        Log.i("Pending Intent","" + pendingIntent.getCreatorPackage());

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.
        final Notification.Builder builder = new Notification.Builder(context);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_HIGH);

        // Set notification content intent to take user to fullscreen UI if user taps on the
        // notification body.
        builder.setContentIntent(pendingIntent);
        // Set full screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.setFullScreenIntent(pendingIntent, true);

        // Setup notification content.
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Your notification title");
        builder.setContentText("Your notification content.");

        // Set notification as insistent to cause your ringtone to loop.
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_INSISTENT;

        // Use builder.addAction(..) to add buttons to answer or reject the call.
        NotificationManager notificationManager = context.getSystemService(
                NotificationManager.class);
        notificationManager.notify("Call Notification", 37, notification);

        //        context.startActivity(intent);


    }
}
