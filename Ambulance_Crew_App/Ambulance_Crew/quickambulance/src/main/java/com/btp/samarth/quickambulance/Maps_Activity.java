package com.btp.samarth.quickambulance;

/**
 * Created by Samarth on 21-10-2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class Maps_Activity extends Activity {

    // Google Map

    private GoogleMap googleMap;
    String details;
    String name;
    String contact;
    String lati;
    String longi;
    String email;
    String regId;
    Double latitude, longitude;
    ImageButton imageButton;
    TextView textView,textView2;
//    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        imageButton=(ImageButton)findViewById(R.id.imageButton);
//        btnNext = (Button) findViewById(R.id.buttonQue);

        Intent i = getIntent();
        // getting attached intent data
        details = i.getStringExtra("message");
        Log.i("user_data1",details);

        try {
            JSONObject jObject = new JSONObject(details);
            name = jObject.getString("name");
            contact = jObject.getString("contact");
            lati = jObject.getString("lati");
            longi = jObject.getString("longi");
            Log.i("user_data","lati: "+lati+" longi: "+longi);
            textView.setText(name);
            textView2.setText(contact);
        } catch (JSONException e1) {
            e1.printStackTrace();
            // Toast.makeText(getBaseContext(), "No item Found",
            // Toast.LENGTH_LONG).show();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        try {
            // Loading map
            latitude = Double.valueOf(lati);;
            longitude = Double.valueOf(longi);
            initilizeMap();
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

            googleMap.clear();
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Ambulance ");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));

            googleMap.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } catch (Exception e) {
            e.printStackTrace();
        }

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View arg0){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+91" + contact));
                startActivity(callIntent);
            }
        });

//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                // Launch Main Activity
//                Intent i = new Intent(getApplicationContext(), QuestionnaireActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
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
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

}
