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
import android.support.v4.app.NotificationCompat;
import android.telecom.Connection;
import android.util.Log;

//@RequiresApi(api = Build.VERSION_CODES.M)
//https://developer.android.com/reference/android/os/Build.VERSION_CODES#M
public class CallConnection extends Connection{
    private static String TAG = "CallConnection";

    private final Context context;

    //Android 7.1 in October 2016 for Developers "Nougat" API 25
    //for setConnectionProperties(PROPERTY_SELF_MANAGED);
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public CallConnection(Context con){
    //public CallConnection(){
        context = con;
        setConnectionProperties(PROPERTY_SELF_MANAGED);
        //setAudioModeIsVoip(true); //???
        //setActive();

    }

    @Override
    public void onAnswer(){
        Log.d(TAG, "onAnswer() called");
        //Accept the Call
        this.setActive();
    }

    @RequiresApi(api = Build.VERSION_CODES.O) //Android Oreo 8.0 August 2017
    @Override
    public void onShowIncomingCallUi() {
        //https://stackoverflow.com/questions/62631787/android-connectionservice-incoming-call-ui-not-showing-onshowincomingcallui

        Log.i(TAG,"onShowIncomingCallUi() : Incoming Call");

        super.onShowIncomingCallUi();

        //
        //https://developer.android.com/reference/android/telecom/Connection#onShowIncomingCallUi()
        //
        //        MainActivity con = new MainActivity();
        //        Context context = con.getApplicationContext();

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Android 8.0 Oreo in August 2017
            channel = new NotificationChannel("channel", "Incoming Calls",
                    NotificationManager.IMPORTANCE_HIGH);
              //channel = new NotificationChannel(YOUR_CHANNEL_ID, "Incoming Calls",
              //channel = new NotificationChannel("channel", "Incoming Calls",
              //      NotificationManager.IMPORTANCE_MAX);
            // other channel setup stuff goes here.

        }
        assert channel != null;
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

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        //
        // Create an intent which triggers your fullscreen incoming call user interface.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(context, IncomingCallScreenActivity.class);
        intent.setClass(context, MainActivity.class);
        intent.setClass(context, CallInterceptionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE_UNAUDITED);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE);

        Log.i(TAG,"onShowIncomingCallUi() Intent1 " + intent);
        Log.i(TAG,"onShowIncomingCallUi() Intent2 " + intent.getPackage());
        Log.i(TAG,"onShowIncomingCallUi() Intent3 " + intent.getType());
        Log.i(TAG,"onShowIncomingCallUi() Intent4 " + intent.getData());
        Log.i(TAG,"onShowIncomingCallUi() Intent5 " + intent.getDataString());
        Log.i(TAG,"onShowIncomingCallUi() Intent6 " + intent.getAction());
        Log.i(TAG,"onShowIncomingCallUi() Intent7 " + intent.getCategories());
        Log.i(TAG,"onShowIncomingCallUi() Intent8 " + intent.getExtras());

        Log.i(TAG,"onShowIncomingCallUi() Pending Intent : " + pendingIntent);
        Log.i(TAG,"onShowIncomingCallUi() Pending Intent : " + pendingIntent.getCreatorPackage());


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
        builder.setContentTitle("drtjdrtejdstdrjtjdrtjdrt(");
        builder.setContentText("Your notification content.");
        Log.i(TAG,"onShowIncomingCallUi(), builder Extras are " + builder.getExtras().toString());
        Log.i(TAG,"onShowIncomingCallUi(), builder is " + builder.toString());

        // Set notification as insistent to cause your ringtone to loop.
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_INSISTENT;
        Log.i(TAG,"onShowIncomingCallUi(), notification getChannelId is " + notification.getChannelId());
        Log.i(TAG,"onShowIncomingCallUi(), notification is " + notification.toString());

        // Use builder.addAction(..) to add buttons to answer or reject the call.
        //NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify("Call Notification", 37, notification);
        //notificationManager.notify(String.valueOf(channel.getName()), Integer.parseInt(channel.getId()), notification);
        //notificationManager.notify(notification.getChannelId(), Integer.parseInt(channel.getId()), notification);

        Log.i(TAG,"onShowIncomingCallUi()/notificationManager() is " + notificationManager.toString());
        //        context.startActivity(intent);
        //

        //-----------------------------------------------------------------------------------------------------------------

        //Autre test ==> NOK, deprecated sur Notification
        //https://www.tabnine.com/code/java/methods/android.app.NotificationManager/notify

//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        //Notification notification = new Notification(R.drawable.ic_launcher,
//        Notification notification = new Notification(R.mipmap.ic_launcher,
//                "Hello from service", System.currentTimeMillis());
//        Intent intent2 = new Intent(this, CallInterceptionActivity.class);
//        notification.setLatestEventInfo(this, "contentTitle", "contentText",
//                PendingIntent.getActivity(this, 1, intent2, 0));
//        manager.notify(111, notification);

        //Autre test ==> NOK, rien d'afficher
        //https://stackoverflow.com/questions/16045722/android-notification-is-not-showing

//        NotificationManager mNotificationManager;
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
//        Intent ii = new Intent(context.getApplicationContext(), CallInterceptionActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);
//
//        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//        //bigText.bigText(verseurl); // ???
//        bigText.bigText("Test TEste test");
//        bigText.setBigContentTitle("Today's Bible Verse");
//        bigText.setSummaryText("Text in detail");
//
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
//        mBuilder.setContentTitle("Your Title");
//        mBuilder.setContentText("Your text");
//        mBuilder.setPriority(Notification.PRIORITY_MAX);
//        mBuilder.setStyle(bigText);
//
//        mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//// === Removed some obsoletes
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            String channelId = "Your_channel_id";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//
//            mNotificationManager.createNotificationChannel(channel);
//            mBuilder.setChannelId(channelId);
//        }//
//
//        mNotificationManager.notify(0, mBuilder.build());



    }
}
