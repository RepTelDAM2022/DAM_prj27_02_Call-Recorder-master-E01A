package com.sam.callrecorder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallInterception extends BroadcastReceiver {
    private static final String TAG = "CallInterception ";
    private TelecomManager telecomManager;
    private Context myContext;
    private PhoneAccountHandle phoneAccountHandle=null;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Gestion de la telephony pour recevoir les appels entrants
        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        tmgr.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        myContext = context;

        //Intent serviceIntent = new Intent(context, CallRecorder.class);
        //Toast.makeText(context,"CallInterception : starting CallRecorder service.",Toast.LENGTH_LONG).show();
        //Log.i(TAG, "CallInterception : starting CallRecorder service.");
        //context.startService(serviceIntent);

        //Gestion du Telecom Manager et d'un gestionnaire de comptes d'appels téléphoniques
        telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        if (phoneAccountHandle == null) {
            Log.i(TAG, "onReceive(), phoneAccountHandle.getId() est null");

            phoneAccountHandle = new PhoneAccountHandle(
                    //new ComponentName(this.getApplicationContext(), MyConnectionService.class),
                    //new ComponentName(MainActivity.this, MyConnectionService.class),
                    new ComponentName(myContext, MyConnectionService.class),
                    //new    ComponentName(contextLocal.getApplicationContext().getPackageName(),MyConnectionService.class.getName()),
                    "examplee");

            Log.i(TAG, "onReceive(), phoneAccountHandle.getId() est " + phoneAccountHandle.getId());

            //PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "examplee").setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
            //ou
            PhoneAccount.Builder builder = new PhoneAccount.Builder(phoneAccountHandle, "examplee");
            builder.setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER);
            //builder.setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED); //si décommenté, n'active pas le onShowIncomingCallUi(), dommage !
            PhoneAccount phoneAccount = builder.build();
            //.setCapabilities(PhoneAccount..CAPABILITY_CONNECTION_MANAGER)

            telecomManager.registerPhoneAccount(phoneAccount);
        } else {
            Log.i(TAG, "onReceive(), phoneAccountHandle.getId() est " + phoneAccountHandle.getId());
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        //private Context contextLocal;

        @SuppressLint("ObsoleteSdkInt")
        @RequiresApi(api = Build.VERSION_CODES.O) //Android Oreo 8.0 August 2017
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String time =  dateFormat.format(new Date()) ;

            Log.i(TAG, "onCallStateChanged(), time is "+ time);

            File sampleDir = new File(Environment.getExternalStorageDirectory(), "/callrecorder");
            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
                Log.i(TAG, "onCallStateChanged(), directory created is "+ sampleDir.getAbsolutePath());
            }
            Log.i(TAG, "onCallStateChanged(), directory created is "+ sampleDir.getPath());


            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // CALL_STATE_IDLE;
                    Log.i(TAG, "onCallStateChanged: IDLE " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // CALL_STATE_OFFHOOK;
                    Log.i(TAG, "onCallStateChanged: OFF HOOK " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // CALL_STATE_RINGING
                    //https://stackoverflow.com/questions/43027292/addincomingcall-in-android-telecommanager-not-doing-anything
                    Log.i(TAG, "onCallStateChanged: RINGING " + incomingNumber);

                    if (incomingNumber != null) {
                        //tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);

                        //Gestion du Handle du compte de téléphone
                        //if (phoneAccountHandle == null) {

                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Android 6.0 in October 2015 Marshmallow API 23
//                            phoneAccountHandle = new PhoneAccountHandle(
//                                    //new ComponentName(this.getApplicationContext(), MyConnectionService.class),
//                                    //new ComponentName(MainActivity.this, MyConnectionService.class),
//                                    new ComponentName(myContext, MyConnectionService.class),
//                                    //new    ComponentName(contextLocal.getApplicationContext().getPackageName(),MyConnectionService.class.getName()),
//                                    "examplee");
//                            //}
//
//                            Log.i(TAG, "onReceive(), phoneAccountHandle.getId() est " + phoneAccountHandle.getId());
//
//                            //PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "examplee").setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
//                            //ou
//                            PhoneAccount.Builder builder = new PhoneAccount.Builder(phoneAccountHandle, "examplee");
//                            builder.setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER);
//                            //builder.setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED); //si décommenté, n'active pas le onShowIncomingCallUi(), dommage !
//                            PhoneAccount phoneAccount = builder.build();
//                            //.setCapabilities(PhoneAccount..CAPABILITY_CONNECTION_MANAGER)
//
//                            telecomManager.registerPhoneAccount(phoneAccount);


                            //Intent intent2=new Intent();
                            //intent2.setClassName("com.android.server.telecom","com.android.server.telecom.settings.EnableAccountPreferenceActivity");
                            //context.startActivity(intent2);
                        //} else {
                            //Log.i(TAG, "onReceive(), phoneAccountHandle.getId() est " + phoneAccountHandle.getId() + " User handle est " + phoneAccountHandle.getUserHandle().toString());

                            //TODO : contrôler que le PhoneAccountHandle est enabled
                            //Sinon demander d'activer, mais ici il y a un message d'erreur,
                            // android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
                            //Intent intent2=new Intent();
                            //intent2.setClassName("com.android.server.telecom","com.android.server.telecom.settings.EnableAccountPreferenceActivity");
                            //myContext.startActivity(intent2);


                            //Gestion de l'appel de addNewIncomingCall() vers le Système (smartphone)
                            Bundle extras = new Bundle();

                            //Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, mNumber.getText().toString(), null);
                            Log.i(TAG," on onCallStateChanged(), tm.getLine1Number(phoneAccountHandle) is " + telecomManager.getLine1Number(phoneAccountHandle));
                            //Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, tm.getLine1Number(phoneAccountHandle), null);

                            Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, incomingNumber.toString(), null);
                            Log.i(TAG, "URI est " + uri.toString());

                            extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
                            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);

                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //    if(tm.isIncomingCallPermitted(phoneAccountHandle)) {
                            telecomManager.addNewIncomingCall(phoneAccountHandle, extras);
                            Log.i(TAG, "Call system via tm.addNewIncomingCall(), done");
                            //   } //requires permission MANAGE_OWN_CALLS
                            //}

                        //}

                    } else {
                        Log.i(TAG, "Evite msg d'erreur : incommingNumber est null");
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
