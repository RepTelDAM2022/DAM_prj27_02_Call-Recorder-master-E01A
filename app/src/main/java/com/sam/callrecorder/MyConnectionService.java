package com.sam.callrecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

//public class MyConnectionService extends Service {
@RequiresApi(api = Build.VERSION_CODES.M)
public class MyConnectionService extends ConnectionService {

    private static String TAG = "MyConnectionService";

    Context context;

    public MyConnectionService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.i(TAG,"onCreateIncomingConnection() returns a callConnection");
        //return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);

        context = getApplicationContext();

        Log.i("Context","" + context);
        Log.i("Context","" + context.getPackageName());
        Log.i("Context","" + getBaseContext());
        Log.i("Context","" + context.getClass().getName());
        Log.i("Context","" + context.getClass().getSimpleName());

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

        return callConnection;
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
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