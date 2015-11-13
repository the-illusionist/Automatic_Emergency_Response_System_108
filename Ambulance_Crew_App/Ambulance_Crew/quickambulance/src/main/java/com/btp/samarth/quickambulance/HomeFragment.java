package com.btp.samarth.quickambulance;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class HomeFragment extends Fragment {

    private GoogleMap googleMap;
    String name;
    String contact;
    String lati;
    String amb_lati;
    String longi;
    String amb_longi;
    String regId;
    Double latitude, longitude, amb_latitude, amb_longitude;
    ImageButton imageButton;
    TextView textView,textView2;
	
	public HomeFragment(){}
    private static View rootView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView .getParent();
            if (parent != null)
                parent.removeView(rootView );
        }
        try {
            rootView = inflater.inflate(R.layout.activity_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        textView = (TextView)rootView.findViewById(R.id.textView);
        textView2 = (TextView)rootView.findViewById(R.id.textView2);
        imageButton=(ImageButton)rootView.findViewById(R.id.imageButton);

        regId=((MainActivity)getActivity()).regId;
        name=((MainActivity)getActivity()).name;
        contact=((MainActivity)getActivity()).contact;
        lati=((MainActivity)getActivity()).lati;
        longi=((MainActivity)getActivity()).longi;

        textView.setText(name);
        textView2.setText(contact);
        try {
            // Loading map
            latitude = Double.valueOf(lati);;
            longitude = Double.valueOf(longi);
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

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View arg0){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+91" + contact));
                startActivity(callIntent);
            }
        });
         
        return rootView;
    }
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity(),
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
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

            amb_latitude = location.getLatitude();
            amb_longitude = location.getLongitude();
            amb_lati = Double.toString(latitude);
            amb_longi = Double.toString(longitude);
            new HttpAsyncTask().execute("http://172.16.100.139:3001");
        }
    };

    public String POST(String url){
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
            jsonObject.accumulate("reg_id", regId);
            jsonObject.accumulate("lati", amb_lati);
            jsonObject.accumulate("longi", amb_longi);

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
            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();

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
}
