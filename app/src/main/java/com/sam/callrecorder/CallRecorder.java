package com.sam.callrecorder;

import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class CallRecorder extends Service {
        private static final String TAG = "CallRecorder";

        private MediaRecorder recorder;
        private boolean recordStarted = false;
        private String savedNumber;
        public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
        public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
        public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
        private int lastState = TelephonyManager.CALL_STATE_IDLE;
        private boolean isIncoming;

        @Override
        public IBinder onBind(Intent arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onBind()");
            return null;
        }

        @Override
        public void onDestroy() {
            // ....
            Log.i(TAG, "onDestroy()");
            super.onDestroy();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_OUT);
            filter.addAction(ACTION_IN);
            this.registerReceiver(new CallReceiver(), filter);
            Log.i(TAG, "onStartCommand : registered new CallReceiver() Service.");
            return super.onStartCommand(intent, flags, startId);
        }

        private void stopRecording() {
            if (recordStarted) {
                recorder.stop();
                recordStarted = false;
            }
            Log.i(TAG, "stopRecording()");
        }

    public abstract class PhoneCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_OUT)) {
                savedNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
                Log.i(TAG, "intent.getAction().ACTION_OUT, savedNumber is "+savedNumber);
            }  else {
            String stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
             savedNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;

            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = CALL_STATE_RINGING;
            }
                Log.i(TAG, "intent.getAction().ACTION_OUT = false, savedNumber is "+savedNumber);
                onCallStateChanged(context, state, savedNumber);
                Log.i(TAG, "onCallStateChanged()");

        }

        Toast.makeText(context,"PhoneCallReceiver saved number is " + savedNumber,Toast.LENGTH_LONG).show();
        Log.i(TAG, "PhoneCallReceiver saved number is "+savedNumber);
    }

        protected abstract void onIncomingCallReceived(Context ctx, String number);

        protected abstract void onIncomingCallAnswered(Context ctx, String number);

        protected abstract void onIncomingCallEnded(Context ctx, String number);

        protected abstract void onOutgoingCallStarted(Context ctx, String number);

        protected abstract void onOutgoingCallEnded(Context ctx, String number);

        protected abstract void onMissedCall(Context ctx, String number);


        public void onCallStateChanged(Context context, int state, String number) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
            String time =  dateFormat.format(new Date()) ;

            Log.i(TAG, "onCallStateChanged(), time FR is "+ time);

            File sampleDir = new File(Environment.getExternalStorageDirectory(), "/callrecorder");
            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
                Log.i(TAG, "onCallStateChanged(), directory created is "+ sampleDir.getAbsolutePath());
            }
            Log.i(TAG, "onCallStateChanged(), directory created is "+ sampleDir.getPath());
            Log.i(TAG, "onCallStateChanged(), Call state changed to "+ state + " , latest state is " + lastState);

            if (lastState == state) {
                return;
            }
            switch (state) {
                case CALL_STATE_RINGING:
                    isIncoming = true;
                    savedNumber = number;
                    onIncomingCallReceived(context, number );

                    recorder = new MediaRecorder();
                    recorder.setAudioSamplingRate(8000);
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + "Incoming \n" + number + "  \n" + time + "  \n" + " Call.amr");

                    Log.i(TAG, "onCallStateChanged(), CALL_STATE_RINGING is "+ CALL_STATE_RINGING + " MediaRecorder() instanciated" + recorder.toString() );

                    try {
                        recorder.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recorder.start();
                    Log.i(TAG, "onCallStateChanged(), MediaRecorder started");
                    recordStarted = true;

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (lastState != CALL_STATE_RINGING) {
                        isIncoming = false;

                        recorder = new MediaRecorder();
                        recorder.setAudioSamplingRate(8000);
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + "Outgoing \n" + savedNumber + "  \n" + time + "  \n" + " Call.amr");

                        Log.i(TAG, "onCallStateChanged(), in CALL_STATE_RINGING, MediaRecorder() instanciated" + recorder.toString() );

                        try {
                            recorder.prepare();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        recorder.start();
                        recordStarted = true;

                        Log.i(TAG, "onCallStateChanged(), MediaRecorder started, callin onOutGoigngCallStarted(), savedNumber is " + savedNumber);
                        onOutgoingCallStarted(context, savedNumber );

                    } else {
                        isIncoming = true;
                        Log.i(TAG, "onCallStateChanged(), isIncoming = true, calling onIncomingCallAnswered(), savedNumber is " + savedNumber);
                        onIncomingCallAnswered(context, savedNumber);
                    }

                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (lastState == CALL_STATE_RINGING) {
                        onMissedCall(context, savedNumber);
                        Log.i(TAG, "onCallStateChanged(), CALL_STATE_IDLE, calling onMissedCall(), savedNumber is " + savedNumber);
                    } else if (isIncoming) {

                        stopRecording();
                        Log.i(TAG, "onCallStateChanged(), CALL_STATE_IDLE, calling onIncomingCallEnded(), savedNumber is " + savedNumber);
                        onIncomingCallEnded(context, savedNumber);
                    } else {

                        stopRecording();
                        Log.i(TAG, "onCallStateChanged(), CALL_STATE_IDLE, calling onOutgoingCallEnded(), savedNumber is " + savedNumber);
                        onOutgoingCallEnded(context, savedNumber);
                    }
                    break;
            }
            lastState = state;
            Log.i(TAG, "onCallStateChanged(), lastState is " + lastState);
        }

    }

    public class CallReceiver extends PhoneCallReceiver {

        @Override
        protected void onIncomingCallReceived(Context ctx, String number) {
            Log.i(TAG, "CallReceiver() / onIncomingCallReceived(), number is " + number);
        }

        @Override
        protected void onIncomingCallAnswered(Context ctx, String number) {
            Log.i(TAG, "CallReceiver() / onIncomingCallAnswered(), number is " + number);
        }

        @Override
        protected void onIncomingCallEnded(Context ctx, String number) {
            Log.i(TAG, "CallReceiver() / onIncomingCallEnded(), number is " + number);
        }

        @Override
        protected void onOutgoingCallStarted(Context ctx, String number) {
            Log.i(TAG, "CallReceiver() / onOutgoingCallStarted(), number is " + number);
        }

        @Override
        protected void onOutgoingCallEnded(Context ctx, String number) {
            Log.i(TAG, "CallReceiver() / onOutgoingCallEnded(), number is " + number);
        }

        @Override
        protected void onMissedCall(Context ctx, String number) {
            Log.i(TAG, "CallReceiver() / onMissedCall(), number is " + number);
        }
    }

  }

