package com.abhi.quickambulance;

/**
 * Created by Abhishek Kumar, IIT Jodhpur
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

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

public class Main_Activity extends Activity {

	Button btn;
	EditText eT,eT2;
	CheckBox cB;
	// GPSTracker class
	GPSTracker gps;
	LocationManager lm;
	Location location;
	String name,contact,lati,longi,details,email;
	double latitude, longitude;
	int $num;
	User_Info user;
	my_location myLocation = new my_location();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


		Intent i = getIntent();
		// getting attached intent data
		details = i.getStringExtra("json_data");
		Log.i("user_data1",details);

		try {
			JSONObject jObject = new JSONObject(details);
			lati = jObject.getString("lati");
			longi = jObject.getString("longi");
			email = jObject.getString("email");
			Log.i("user_data","lati: "+lati+" longi: "+longi+" email "+email);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

        btn = (Button) findViewById(R.id.button);
		eT = (EditText) findViewById(R.id.editText);
        eT2 = (EditText) findViewById(R.id.editText2);
		cB = (CheckBox) findViewById(R.id.checkBox);
		loadSavedPreferences();
        // show location button click event
        btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Connection_Detector cd = new Connection_Detector(getApplicationContext());
				name = eT.getText().toString().trim();
				contact = eT2.getText().toString().trim();
				String MobilePattern = "[0-9]{10}";
				if (name.equals("")) {
					Toast.makeText(Main_Activity.this,
							"Name Field is empty", Toast.LENGTH_SHORT).show();
				} else if (!contact.toString().matches(MobilePattern)) {
					Toast.makeText(Main_Activity.this,
							"Invalid Mobile number", Toast.LENGTH_SHORT).show();
				} else if (cd.isConnectingToInternet())
				// true or false
				{
					savePreferences("CheckBox_Value", cB.isChecked());
					if (cB.isChecked()) {
						savePreferences("saved_id", eT.getText().toString());
						savePreferences("saved_contact", eT2.getText().toString());
					}

//					}
					lm = (LocationManager) getSystemService(LOCATION_SERVICE);

					if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						//Toast.makeText(Main_Activity.this, "Getting your current Location, Please wait", Toast.LENGTH_LONG).show();
						myLocation.getLocation(getApplicationContext(), locationResult);
					} else {
						showGPSDisabledAlertToUser();
					}

				} else {
					showInternetDisabledAlertToUser();
				}
			}
		});
	}

	public my_location.LocationResult locationResult = new my_location.LocationResult() {

		@Override
		public void gotLocation(Location location) {
			// TODO Auto-generated method stub

			if(location!=null)
			{
				//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lati + "\nLong: " + longi, Toast.LENGTH_LONG).show();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main_Activity.this);
				alertDialogBuilder.setMessage("Are you sure, You want to request an Ambulance?");

				alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Toast.makeText(Main_Activity.this, "Ambulance requested, Please wait", Toast.LENGTH_LONG).show();
						new HttpAsyncTask().execute("http://btp1.iitj.ac.in:3000");
					}
				});

				alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
			else
			{
				Toast.makeText(Main_Activity.this, "User location not found, Please try again", Toast.LENGTH_LONG).show();
			}
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

	private void loadSavedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean checkBoxValue = sharedPreferences.getBoolean("CheckBox_Value", false);
		String saved_id = sharedPreferences.getString("saved_id", "");
		String saved_contact = sharedPreferences.getString("saved_contact", "");
		if (checkBoxValue) {
			cB.setChecked(true);
		} else {
			cB.setChecked(false);
		}

		eT.setText(saved_id);
		eT2.setText(saved_contact);
	}

	private void savePreferences(String key, boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}


	public String POST(String url, User_Info user) {
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
			jsonObject.accumulate("name", user.getName());
			jsonObject.accumulate("contact", user.getContact());
			jsonObject.accumulate("lati", user.getLatitude());
			jsonObject.accumulate("longi", user.getLongitude());
			jsonObject.accumulate("email", email);

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
			user.setName(name);
			user.setContact(contact);
			user.setLatitude(lati);
			user.setLongitude(longi);

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

			// Launching new Activity
			if(result.toLowerCase().contains("name"))
			{
				Intent i = new Intent(getApplicationContext(),
						Maps_Screen.class);
				// sending data to new activity
				i.putExtra("json_data", json_string);
				startActivity(i);
				finish();
			}
			else
			{
				Toast.makeText(getBaseContext(), "No Ambulance available right now, Please try after sometime", Toast.LENGTH_LONG).show();
			}
		}
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}
}