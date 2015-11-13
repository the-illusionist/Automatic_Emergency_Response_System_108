package com.btp.samarth.quickambulance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class SplashScreen extends Activity{
    private static int SPLASH_TIME_OUT = 3000;
    GPSTracker gps;
    public static String lati, longi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Connection_Detector cd = new Connection_Detector(getApplicationContext());

        if (cd.isConnectingToInternet()) {
            gps = new GPSTracker(SplashScreen.this);
            // check if GPS enabled
            double latitude = 0;
            double longitude = 0;
            if (gps.canGetLocation()) {
                //while (latitude == 0) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                //}
                lati=Double.toString(latitude);
                longi=Double.toString(longitude);
                ((Variables) this.getApplication()).Setamb_lati(lati);
                ((Variables) this.getApplication()).Setamb_longi(longi);
                //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("Android Id= ", androidId);

            } else {
                gps.showSettingsAlert();
            }
        }
        else{
            showAlertDialog(SplashScreen.this,
                    "No Internet Connection",
                    "No internet connection.", false);
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over

                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                startActivity(i);
                // close this activity
                //finish();
            }
        }, SPLASH_TIME_OUT);
    }
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}