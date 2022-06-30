package com.sam.callrecorder;

import static android.Manifest.permission.MANAGE_OWN_CALLS;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) //Android 5.0 in November 2014 Lollipop
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //Variables
    Button btn;

    Intent intent;

    int PERMISSION_ALL = 1;
    String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.MANAGE_OWN_CALLS
    };

    //Init
    private void initUI(){
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : gérer des préférence d'activation/désactivation d'un PhoneAccount, voir le lien,
                // https://android.googlesource.com/platform/packages/services/Telecomm/+/5534434/src/com/android/server/telecom/settings/EnableAccountPreferenceFragment.java
                intent = new Intent();
                intent.setClassName("com.android.server.telecom","com.android.server.telecom.settings.EnableAccountPreferenceActivity");
                startActivity(intent);
            }
        });
    }

    //Méthodes applicatives
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    //Méthodes Cycle de vie
    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            Log.i(TAG,"Permission allowed.");
        }

//        TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
//
//        //Gestion du Handle de compte Tél. et appel à la méthode addNewIncomingCall(..)
//        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(
//                //new ComponentName(this.getApplicationContext(), MyConnectionService.class),
//                new ComponentName(MainActivity.this, MyConnectionService.class),
//                "examplee");
//
//        PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "examplee").setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
//        tm.registerPhoneAccount(phoneAccount);

        //Tests,
        // dans la classe "CallInterception" / appel à la méthode tm.addNewIncomingCall(phoneAccountHandle, extras) affiche
        // le message d'erreur: "java.lang.SecurityException: This PhoneAccountHandle is not enabled for this user!"
        // Il faudrait l'activer au moins une fois
        //
        //https://stackoverflow.com/questions/65794085/how-to-fix-java-lang-securityexception-this-phoneaccounthandle-is-not-enabled-f
        //
        //Test 01,
        //Intent intent=new Intent();
        //intent.setClassName("com.android.server.telecom","com.android.server.telecom.settings.EnableAccountPreferenceActivity");
        //startActivity(intent);//

        //Test 02,
        /*if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.server.telecom",
                    "com.android.server.telecom.settings.EnableAccountPreferenceActivity"));
            startActivity(intent);
        }else {
            //startActivity(new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS));
        }*/

//
//        Bundle extras = new Bundle();
//
//        //Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, mNumber.getText().toString(), null);
////        Log.i(TAG," on click phoneNumber is " + tm.getLine1Number(phoneAccountHandle));
//        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, tm.getLine1Number(phoneAccountHandle), null);
////        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "0777392996", null);
//        Log.i(TAG, "onCreate: " + uri);
//
//        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
//        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
//
//        tm.addNewIncomingCall(phoneAccountHandle, extras);

        //Gestion du bouton
        //btn = (Button) findViewById(R.id.btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            //@RequiresApi(api = Build.VERSION_CODES.M)
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"Version SDK_INT is " + Build.VERSION.SDK_INT + "Version_CODE.M is " + Build.VERSION_CODES.M,Toast.LENGTH_LONG).show();
//                Log.i(TAG, "Version SDK_INT is " + Build.VERSION.SDK_INT + " Version_CODE.M is " + Build.VERSION_CODES.M);
//
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                }

//                Intent serviceIntent = new Intent(MainActivity.this, CallRecorder.class);
//                Log.i(TAG,"startService CallRecorder intent." );
//                startService(serviceIntent);

        Intent serviceIntent = new Intent(MainActivity.this, MyConnectionService.class);
        Log.i(TAG,"startService MyConnectionService intent." );
        startService(serviceIntent);
    }


    //Méthodes de tests pour Telephony, Voicemail et Camera
    public void testDialVoicemail() {
        PackageManager packageManager = this.getApplicationContext().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Uri uri = Uri.parse("voicemail:");
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            Log.i(TAG,"testDialVoicemail(), Feature Telephony intent : " );intent.getAction();
            //assertCanBeHandled(intent);
        }
    }

    /**
     * Test ACTION_SHOW_CALL_SETTINGS, it will display the call preferences.
     */
    public void testShowCallSettings() {
        PackageManager packageManager = this.getApplicationContext().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Intent intent = new Intent(TelecomManager.ACTION_SHOW_CALL_SETTINGS);
            Log.i(TAG,"testShowCallSettings(), Feature Telephony Settings intent : " );intent.getAction();
            //assertCanBeHandled(intent);
        }
    }

    /**
     * Test start camera by intent
     */
    public void testCamera() {
        PackageManager packageManager = this.getApplicationContext().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.i(TAG, "testCamera(), Feature Camera Front intent : " + intent.getAction());

            //assertCanBeHandled(intent);

            //intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        }
    }

    //});
}


