package com.btp.samarth.quickambulance;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Abhi on 10-11-2015.
 */
public class QuestionnaireActivity extends Activity{
    TextView tV1,tV2,tV3,tV4,tV5,tV6,tV7,tV8,tV9,tV10;
    Switch s1,s2,s3,s4,s5,s6,s7,s8,s9,s10;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
    }

}
