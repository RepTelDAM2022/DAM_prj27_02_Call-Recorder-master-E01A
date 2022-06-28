package com.sam.callrecorder;

import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//public class MyConnectionService extends Service {
@RequiresApi(api = Build.VERSION_CODES.M)
public class MyConnectionService extends ConnectionService {

    private static String TAG = "MyConnectionService";

    Context context;
    MediaRecorder recorder;

    public MyConnectionService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.i(TAG,"onCreateIncomingConnection() returns a callConnection");
        //return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);

        context = getApplicationContext();

        Log.i(TAG," Context " + context);
        Log.i(TAG," Context " + context.getPackageName());
        Log.i(TAG," Context " + getBaseContext());
        Log.i(TAG," Context " + context.getClass().getName());
        Log.i(TAG," Context " + context.getClass().getSimpleName());

        CallConnection callConnection = new CallConnection(context);
        callConnection.setInitializing();
        Log.i(TAG,"onCreateIncomingConnection() getCallerDisplayName est " + callConnection.getCallerDisplayName());
        Log.i(TAG,"onCreateIncomingConnection() getAddress est " + callConnection.getAddress());
        Log.i(TAG,"onCreateIncomingConnection() getExtras est " + callConnection.getExtras() + "\n");

        Log.i(TAG,"onCreateIncomingConnection() UserHandle est " + connectionManagerPhoneAccount.getUserHandle());
        Log.i(TAG,"onCreateIncomingConnection() PhoneAccount est " + connectionManagerPhoneAccount.toString());
        Log.i(TAG,"onCreateIncomingConnection() ComponentName est " + connectionManagerPhoneAccount.getComponentName());
        Log.i(TAG,"onCreateIncomingConnection() getId est " + connectionManagerPhoneAccount.getId() + "\n");

        Log.i(TAG,"onCreateIncomingConnection() request est " + request.toString());
        Log.i(TAG,"onCreateIncomingConnection() request Address est " + request.getAddress());
        Log.i(TAG,"onCreateIncomingConnection() request Extra est " + request.getExtras());

        callConnection.setActive();

        //callRecorder(request.getAddress().toString());

        return callConnection;
    }


    //public void callRecorder(Context context, int state, String number) {
    public void callRecorder(String number) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        String time = dateFormat.format(new Date());

        Log.i(TAG, "callRecorder(), time is " + time);

        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/callrecorder");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
            Log.i(TAG, "callRecorder(), directory created is " + sampleDir.getAbsolutePath());
        }
        Log.i(TAG, "callRecorder(), directory created is " + sampleDir.getPath());

                recorder = new MediaRecorder();
                recorder.setAudioSamplingRate(8000);
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // ???
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + "Incoming \n" + number + "  \n" + time + "  \n" + " Call.amr");

                Log.i(TAG, "onCallStateChanged(), CALL_STATE_RINGING is " + CALL_STATE_RINGING + " MediaRecorder() instanciated" + recorder.toString());

                try {
                    recorder.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recorder.start();
                Log.i(TAG, "onCallStateChanged(), MediaRecorder started");
                //recordStarted = true;
    }


    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
        recorder.stop();
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
        recorder.stop();
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
    }


    /*@Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }*/
}