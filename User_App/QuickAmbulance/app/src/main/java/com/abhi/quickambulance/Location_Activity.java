package com.abhi.quickambulance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class Location_Activity extends Activity {
    private GoogleMap googleMap;
    String name;
    String contact;
    String lati;
    String email;
    String longi;
    String regId;
    Double latitude, longitude;
    ImageButton imageButton,imageButton2;
    LatLng loc;
    Boolean flag=true;
    my_location myLocation = new my_location();
    LocationManager lm;
    String json_string="";
    JSONObject jsonObject;
    Handler h;
    int delay,cnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Intent i = getIntent();
        // getting attached intent data
        email = i.getStringExtra("data");
        imageButton=(ImageButton)findViewById(R.id.imageButton);
        imageButton2=(ImageButton)findViewById(R.id.imageButton2);
        try {
            // Loading map
            initializeMap();
            // latitude and longitude
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // create marker
            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            //location change listener
            googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

            googleMap.clear();
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("User ");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));

            googleMap.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        } catch (Exception e) {
            e.printStackTrace();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Connection_Detector cd = new Connection_Detector(getApplicationContext());

                if (cd.isConnectingToInternet())
                // true or false
                {
                    lm = (LocationManager) getSystemService(LOCATION_SERVICE);

                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        AsyncTaskRunner runner = new AsyncTaskRunner();
                        String sleepTime = "16000";
                        runner.execute(sleepTime);
                        h = new Handler();
                        delay = 3000; //milliseconds
                        cnt=0;
                        h.postDelayed(new Runnable() {
                            public void run() {
                                googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
                                cnt = cnt + 1;
                                if (cnt < 6) {
                                    h.postDelayed(this, delay);
                                    Log.i("abhi2", Integer.toString(cnt));
                                    Log.i("abhi2_lati", lati);
                                    Log.i("abhi2_longi", longi);
                                }
                            }
                        }, delay);

                    } else {
                        showGPSDisabledAlertToUser();
                    }

                } else {
                    showInternetDisabledAlertToUser();
                }
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getApplicationContext(),
                        FAQ_Activity.class);
                // sending data to new activity
                i.putExtra("json_data", json_string);
                startActivity(i);
            }
        });

    }
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeMap();
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lati = Double.toString(latitude);
            longi = Double.toString(longitude);

            googleMap.clear();
            MarkerOptions marker = new MarkerOptions().position(loc).title("User ");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));

            googleMap.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    };

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

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                int time = Integer.parseInt(params[0]);
                // Sleeping for given time period
                Thread.sleep(time);
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        ProgressDialog dialog = new ProgressDialog(Location_Activity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Determining Accurate Location");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.i("abhi2", Integer.toString(cnt));
            googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
            try{
                jsonObject = new JSONObject();
                jsonObject.accumulate("email", email);
                jsonObject.accumulate("lati", lati);
                jsonObject.accumulate("longi", longi);
                // 4. convert JSONObject to JSON to String
                json_string = jsonObject.toString();
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            Intent i = new Intent(getApplicationContext(),
                    Main_Activity.class);
            // sending data to new activity
            i.putExtra("json_data", json_string);
            startActivity(i);
        }
        @Override
        protected void onProgressUpdate(String... text) {

            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
