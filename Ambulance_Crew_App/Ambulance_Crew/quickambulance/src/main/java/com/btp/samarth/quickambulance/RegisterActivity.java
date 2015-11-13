package com.btp.samarth.quickambulance;

/**
 * Created by Samarth on 05-11-2015.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import static com.btp.samarth.quickambulance.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.btp.samarth.quickambulance.CommonUtilities.EXTRA_MESSAGE;
import static com.btp.samarth.quickambulance.CommonUtilities.SENDER_ID;
import static com.btp.samarth.quickambulance.CommonUtilities.SERVER_URL;

public class RegisterActivity extends Activity {
    // alert dialog manager
    String lati,longi;
    String name = "sam";
    String email = "sam";
    TextView lblMessage;

    Connection_Detector cd;

    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector

    // Register button
    Button btnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        lati = ((Variables) this.getApplication()).getamb_lati();
        longi = ((Variables) this.getApplication()).getamb_longi();
        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lati + "\nLong: " + longi, Toast.LENGTH_LONG).show();
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
        cd = new Connection_Detector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(RegisterActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
            return;
        }

        btnRegister = (Button) findViewById(R.id.btnRegister);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GCMRegistrar.checkDevice(RegisterActivity.this);
//
                // Make sure the manifest was properly set - comment out this line
                // while developing the app, then uncomment it when it's ready.
                GCMRegistrar.checkManifest(RegisterActivity.this);

                lblMessage = (TextView) findViewById(R.id.lblMessage);



                // Get GCM registration id
                final String regId = GCMRegistrar.getRegistrationId(RegisterActivity.this);

                // Check if regid already presents
                if (regId.equals("")) {
                    // Registration is not present, register now with GCM
                    GCMRegistrar.register(RegisterActivity.this, SENDER_ID);
                }
                else {
                    // Device is already registered on GCM
                    if (GCMRegistrar.isRegisteredOnServer(RegisterActivity.this))
                    {
                        // Skips registration.
                        Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(RegisterActivity.this, WaitingActivity.class);
                        i.putExtra("regId",regId);
                        startActivity(i);
                    }
                    else
                    {
                        final Context context = RegisterActivity.this;
                        mRegisterTask = new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                // Register on our server
                                // On server creates a new user
                                ServerUtilities.register(context, name, email, regId);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                mRegisterTask = null;
                                Intent i = new Intent(RegisterActivity.this, DetailsActivity.class);
                                i.putExtra("regId",regId);
                                startActivity(i);
                            }

                        };
                        mRegisterTask.execute(null, null, null);
                    }
                }
            }
        });
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            Log.i("yo ", newMessage);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
//            if(newMessage.length()>0 && newMessage.toLowerCase().contains("name"))
//            {
//                Intent k = new Intent(RegisterActivity.this, MainActivity.class);
//                k.putExtra("message", GCMIntentService.message);
//                startActivity(k);
//            }
            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

}
