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
	String name,contact,lati,longi;

	int $num;
	User_Info user;
	String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Intent i = getIntent();
		// getting attached intent data
		email = i.getStringExtra("data");
		Log.i("MyActivity1",email);

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
				name=eT.getText().toString().trim();
				contact=eT2.getText().toString().trim();
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

					gps = new GPSTracker(Main_Activity.this);
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
						Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
					} else {
						gps.showSettingsAlert();
					}

					new HttpAsyncTask().execute("http://172.16.100.139:3000");
				} else {
					showAlertDialog(Main_Activity.this,
							"No Internet Connection",
							"No internet connection.", false);
				}
			}
		});
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
			Toast.makeText(getBaseContext(), "Info Sent!", Toast.LENGTH_LONG).show();

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

			// Launching new Activity on selecting single List Item
			Intent i = new Intent(getApplicationContext(),
					Maps_Screen.class);
			// sending data to new activity
			i.putExtra("json_data", json_string);
			startActivity(i);
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