package com.abhi.quickambulance;

/**
 * Created by Abhishek Kumar, IIT Jodhpur
 */

import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class Maps_Screen extends Activity {
    TextView textView,textView2,textView3;
    ImageButton imageButton;
    double latitude,longitude;
    // Google Map
    private GoogleMap googleMap;
    String details,name,contact,lati,longi,vehicle_num,id;
    int $num,end_var=0;
    User_Info user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent i = getIntent();
        // getting attached intent data
        details = i.getStringExtra("json_data");
        Log.i("user_data1",details);

        try {
            JSONObject jObject = new JSONObject(details);
            name = jObject.getString("name");
            contact = jObject.getString("contact");
            lati = jObject.getString("lati");
            longi = jObject.getString("longi");
            vehicle_num = jObject.getString("vehicle");
            id = jObject.getString("_id");
            Log.i("user_data","lati: "+lati+" longi: "+longi+" veh "+vehicle_num);
        } catch (JSONException e1) {
            e1.printStackTrace();
            // Toast.makeText(getBaseContext(), "No item Found",
            // Toast.LENGTH_LONG).show();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }


        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        imageButton=(ImageButton)findViewById(R.id.imageButton);
        try {
            textView.setText(name);
            textView2.setText(vehicle_num);
            textView3.setText(contact);
            latitude = Double.valueOf(lati);
            longitude = Double.valueOf(longi);

            // Loading map
            initilizeMap();

            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

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
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude))
                    .title("Ambulance ");
            marker.icon(BitmapDescriptorFactory.
                    fromResource(R.drawable.ic_launcher));

            googleMap.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude,
                            longitude)).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));


            Timer timer = new Timer();
            TimerTask timerTask;
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    new HttpAsyncTask().execute("http://172.16.100.139:3000");
                }
           };
            timer.schedule(timerTask, 0, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View arg0){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+91" + textView3.getText().toString().trim()));
                startActivity(callIntent);
            }
        });
    }

    public String POST(String url, User_Info user){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", id);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            user=new User_Info();
            user.setId(id);
            return POST(urls[0],user);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Info Sent!", Toast.LENGTH_LONG).show();

            // parsing data
            String json_string="";
            try {
                JSONObject jObject1= new JSONObject(result);
                json_string= jObject1.toString();

            } catch (JSONException e1) {
                e1.printStackTrace();
                // Toast.makeText(getBaseContext(), "No item Found",
                // Toast.LENGTH_LONG).show();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            try {
                JSONObject jObject = new JSONObject(result);
//                name = jObject.getString("name");
//                contact = jObject.getString("contact");
                if(end_var==1)
                {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                else if(jObject.getString("isActive")=="true")
                {
                    Toast.makeText(getBaseContext(), "Reached Successfully to the Hospital", Toast.LENGTH_LONG).show();
                    end_var=1;
                }
                else {
                    lati = jObject.getString("lati");
                    longi = jObject.getString("longi");
                    id = jObject.getString("_id");
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            latitude = Double.valueOf(lati);
            longitude = Double.valueOf(longi);

            googleMap.clear();

            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude))
                    .title("Ambulance ");
            marker.icon(BitmapDescriptorFactory.
                    fromResource(R.drawable.ic_launcher));

            googleMap.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude,
                            longitude)).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    /**
     * function to load map If map is not created it will create it for you
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

}

