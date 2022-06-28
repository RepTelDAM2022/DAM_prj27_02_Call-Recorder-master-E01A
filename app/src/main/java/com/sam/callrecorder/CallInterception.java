package com.sam.callrecorder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallInterception extends BroadcastReceiver {
    private static final String TAG = "CallInterception : ";
    public static TelecomManager tm;
    public static Context contextLocal;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        tmgr.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        contextLocal = context;
    }
    private static class MyPhoneStateListener extends PhoneStateListener {

        //private Context contextLocal;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
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
                    Log.i(TAG, "onCallStateChanged: RINGING " + incomingNumber);

                    //tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);

                    //Gestion du Handle de compte Tél. et appel à la méthode addNewIncomingCall(..)
                    PhoneAccountHandle phoneAccountHandle = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        phoneAccountHandle = new PhoneAccountHandle(
                                //new ComponentName(this.getApplicationContext(), MyConnectionService.class),
                                //new ComponentName(MainActivity.this, MyConnectionService.class),
                                new ComponentName(contextLocal,
                                        MyConnectionService.class),
                                    "examplee");
                    }

                    PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "examplee")
                            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                            //.setCapabilities(PhoneAccount..CAPABILITY_CONNECTION_MANAGER)
                            .build();

                    tm.registerPhoneAccount(phoneAccount);

//        Intent intent=new Intent();
//        intent.setClassName("com.android.server.telecom","com.android.server.telecom.settings.EnableAccountPreferenceActivity");
//        startActivity(intent);

                    Bundle extras = new Bundle();

                    //Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, mNumber.getText().toString(), null);
                    Log.i(TAG," on onCallStateChanged phoneNumber is " + tm.getLine1Number(phoneAccountHandle));
                    //Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, tm.getLine1Number(phoneAccountHandle), null);
                    Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, incomingNumber.toString(), null);
                    Log.i(TAG, "URI est " + uri.toString());

                    extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
                    extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);

                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //    if(tm.isIncomingCallPermitted(phoneAccountHandle)) {
                            tm.addNewIncomingCall(phoneAccountHandle, extras);
                            Log.i(TAG, "Call system via tm.addNewIncomingCall(), done");
                     //   } //requires permission MANAGE_OWN_CALLS
                    //}

                    break;
                default:
                    break;
            }
        }
    }
}
