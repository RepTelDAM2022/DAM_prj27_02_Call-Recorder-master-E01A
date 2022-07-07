package com.sam.callrecorder;

import static android.app.PendingIntent.getActivity;
import static android.telecom.DisconnectCause.UNKNOWN;

import android.annotation.SuppressLint;
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
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;

import java.io.Serializable;

//@RequiresApi(api = Build.VERSION_CODES.M)
//https://developer.android.com/reference/android/os/Build.VERSION_CODES#M
public class CallConnection extends Connection {
    private static String TAG = "CallConnection";

    private final Context context;
    private NotificationChannel channel = null;

    /** Définir un mode de fonctionnement en CALLS_SELF_MANAGED
    * Android 7.1 in October 2016 for Developers "Nougat" API 25
    * for setConnectionProperties(PROPERTY_SELF_MANAGED);
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public CallConnection(Context con){
    //public CallConnection(){
        context = con;
        setConnectionProperties(PROPERTY_SELF_MANAGED); //Utile sinon pas d'appel à onShowIncomingCallUi()
        //setAudioModeIsVoip(true); //On est en voice call
    }

    /** Gestion d'un clic sur btn "Répondre" à l'appel intercepté
     * Il peut y avoir d'autre cas d'appel à cette méthode (ex. HOLD)
     */
    @Override
    public void onAnswer(){
        Log.d(TAG, "\nonAnswer() called");
        //Accept the Call
        this.setActive(); //???
    }

    /** Gestion d'un clic sur btn "Reject" de l'appel intercepté
     * Un traitement de différents cas de figure est nécessaire :
     * https://developer.android.com/reference/android/telecom/DisconnectCause
     */
    @Override
    public void onDisconnect(){ //TODO: à continuer le onDisconnect()
        Log.d(TAG, "\nonAnswer() called disconnection processing");
        //Disconnect
        //this.setDisconnected(0);
        this.destroy(); //???
    }

    /** Après que le Système autorise une callConnection
     * l'application peut enclencher une notification et/ou un écran customisé (btn Answer / Reject)
     */
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.O) //Android Oreo 8.0 August 2017 API level 26
    @Override
    public void onShowIncomingCallUi() { //TODO : voir comment controler la notification
        //https://stackoverflow.com/questions/62631787/android-connectionservice-incoming-call-ui-not-showing-onshowincomingcallui

        Log.i(TAG,"onShowIncomingCallUi() : Incoming Call");

        //super.onShowIncomingCallUi();

        //Code du lien URL,
        //https://developer.android.com/reference/android/telecom/Connection#onShowIncomingCallUi()
        //
        //        MainActivity con = new MainActivity();
        //        Context context = con.getApplicationContext();

        //
        //NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { ////SDK_INT est 28 sur ASUS et CODES.O est Android 8.0 Oreo in August 2017 API 26
            channel = new NotificationChannel("channel_Id", "Incoming Calls",
                    NotificationManager.IMPORTANCE_HIGH);
              //channel = new NotificationChannel(YOUR_CHANNEL_ID, "Incoming Calls",
              //channel = new NotificationChannel("channel", "Incoming Calls",
              //      NotificationManager.IMPORTANCE_MAX);
            // other channel setup stuff goes here.
        }
        assert channel != null;
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        // other channel setup stuff goes here.
        Log.i(TAG,"onShowIncomingCallUi() channel est " + channel.toString()
                + "\n Id est " + channel.getId()
                + "\n Description est " + channel.getDescription());

        // We'll use the default system ringtone for our incoming call notification channel.  You can
        // use your own audio resource here.
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //SDK_INT est 28 sur ASUS et CODES.O = 26
            channel.setSound(ringtoneUri, new AudioAttributes.Builder()
                    // Setting the AudioAttributes is important as it identifies the purpose of your
                    // notification sound.
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            Log.i(TAG,"onShowIncomingCallUi() channel.setSound() est " + channel.toString());
        }

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Log.i(TAG,"onShowIncomingCallUi() notificationManager est " + notificationManager.toString());
        //
        // Create an intent which triggers your fullscreen incoming call user interface.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(context, IncomingCallScreenActivity.class);
        intent.setClass(context, CallInterceptionActivity.class);
        PendingIntent pendingIntent = getActivity(context, 1, intent, 0);

        Log.i(TAG,"onShowIncomingCallUi() Intent1 " + intent);
        Log.i(TAG,"onShowIncomingCallUi() Intent2 getPackage() " + intent.getPackage());
        Log.i(TAG,"onShowIncomingCallUi() Intent3 .getType() " + intent.getType());
        Log.i(TAG,"onShowIncomingCallUi() Intent4 .getData() " + intent.getData());
        Log.i(TAG,"onShowIncomingCallUi() Intent5 .getDataString() " + intent.getDataString());
        Log.i(TAG,"onShowIncomingCallUi() Intent6 .getAction() " + intent.getAction());
        Log.i(TAG,"onShowIncomingCallUi() Intent7 .getCategories() " + intent.getCategories());
        Log.i(TAG,"onShowIncomingCallUi() Intent8 .getExtras() " + intent.getExtras());

        Log.i(TAG,"onShowIncomingCallUi() Pending Intent : " + pendingIntent);
        Log.i(TAG,"onShowIncomingCallUi() Pending Intent .getCreatorPackage() " + pendingIntent.getCreatorPackage());
        //Log.i(TAG,"onShowIncomingCallUi() VERSION.SDK_INT est " + Build.VERSION.SDK_INT);
        //Log.i(TAG,"onShowIncomingCallUi() Build.VERSION_CODES.S est " + Build.VERSION_CODES.S);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //SDK_INT est 28 sur ASUS et CODES.S = 31
            if (pendingIntent.isActivity())
                Log.i(TAG, "onShowIncomingCallUi() Pending Intent is Activity ? " + pendingIntent.isActivity());
            else Log.i(TAG, "onShowIncomingCallUi() Pending Intent is not an activity ");
        //}


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
        //builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setSmallIcon(R.mipmap.stagiaire_02);
        builder.setContentTitle("Hello!");
        builder.setContentText("There is an incoming call");
        builder.setChannelId("channel_Id");
        Log.i(TAG,"onShowIncomingCallUi(), builder Extras are " + builder.getExtras().toString());
        Log.i(TAG,"onShowIncomingCallUi(), builder is " + builder.toString());

        // Set notification as insistent to cause your ringtone to loop.
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_INSISTENT;
        Log.i(TAG,"onShowIncomingCallUi(), notification.getChannelId() is " + notification.getChannelId());
        Log.i(TAG,"onShowIncomingCallUi(), notification is " + notification.toString());
        Log.i(TAG,"onShowIncomingCallUi(), notification.contenIntent is " + notification.contentIntent);

        // Use builder.addAction(..) to add buttons to answer or reject the call.
        //NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify("Call Notification", 37, notification);
        //notificationManager.notify(String.valueOf(channel.getName()), Integer.parseInt(channel.getId()), notification);
        //notificationManager.notify(notification.getChannelId(), Integer.parseInt(channel.getId()), notification);

        Log.i(TAG,"onShowIncomingCallUi(), notificationManager() is " + notificationManager.toString());
        //        context.startActivity(intent);
        //
        //this.setActive();
        //Log.i(TAG,"onShowIncomingCallUi(), setActive() here ");

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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //SDK_INT est 28 sur ASUS et CODES.O = 26
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
//


    }
}
