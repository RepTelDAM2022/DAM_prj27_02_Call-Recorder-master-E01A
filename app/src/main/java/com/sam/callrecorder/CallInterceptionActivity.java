package com.sam.callrecorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CallInterceptionActivity extends AppCompatActivity {

    //Variables
    TextView myTvAppel;
    ImageView myIvImage;
    Button myBtnAccepter;
    Button myBtnRejeter;

    //Init
    private void initUI(){
        myTvAppel = findViewById(R.id.tvAppel);
        myIvImage = findViewById(R.id.ivPhoto);
        myBtnAccepter = findViewById(R.id.btnAccepter);
        myBtnRejeter = findViewById(R.id.btnRejeter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_interception);

        initUI();

    }
}