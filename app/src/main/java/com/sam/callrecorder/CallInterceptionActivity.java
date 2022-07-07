package com.sam.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CallInterceptionActivity extends AppCompatActivity {
    private static final String TAG = "CallInterceptionActivity";

    //Variables
    Context context;
    TextView myTvAppel;
    ImageView myIvImage;
    Button myBtnAccepter;
    Button myBtnRejeter;

    /** InitUI
     * Activité pour le traitement customisé d'un appel entrant (btn Answer & btn Reject)
     */
    private void initUI(){
        myTvAppel = findViewById(R.id.tvAppel);
        myIvImage = findViewById(R.id.ivPhoto);
        myBtnAccepter = findViewById(R.id.btnAccepter);
        myBtnRejeter = findViewById(R.id.btnRejeter);

        myBtnAccepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: sur le btn ACCEPTER");
            }
        });

        myBtnRejeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: sur le btn REJETER");
            }
        });

    }

    /**
     * Pour l'instant simple appel à initUI() de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_interception);

        initUI();

    }
}