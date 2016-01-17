package com.abhi.quickambulance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Abhi on 14-08-2015.
 */
public class Splash_Screen extends Activity{
    // Splash screen timer
    int SPLASH_TIME_OUT = 3000;
    int dialog_flag=0;
    int connect_flag=0;
    int toast_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_delay();

    }

    public void new_intent()
    {
        Connection_Detector cd = new Connection_Detector(getApplicationContext());
        if(cd.isConnectingToInternet()) {
            if(connect_flag==1)
            {
                Toast.makeText(Splash_Screen.this, "Connected to Internet now", Toast.LENGTH_LONG).show();
            }
            Intent i = new Intent(Splash_Screen.this, Login_Screen.class);
            startActivity(i);
            // close this activity
            finish();
        }
        else
        {
            if(dialog_flag==0)
            {
                dialog_flag=1;
                connect_flag=1;
                showInternetDisabledAlertToUser();
            }
            if(toast_count==5)
            {
                Toast.makeText(Splash_Screen.this, "Internet Connection still not available", Toast.LENGTH_LONG).show();
                toast_count=0;
            }
            splash_delay();
        }
    }

    public void splash_delay()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app activity_main activity
                new_intent();
                toast_count+=1;
            }
        }, SPLASH_TIME_OUT);
    }

    private void showInternetDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("No Internet Connection. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable Internet",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
