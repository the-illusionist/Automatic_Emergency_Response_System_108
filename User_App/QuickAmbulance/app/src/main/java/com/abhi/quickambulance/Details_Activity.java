package com.abhi.quickambulance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Abhi on 19-11-2015.
 */
public class Details_Activity extends Activity {


    Button btn;
    EditText eT, eT2;
    String name, contact,email, json_string;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        // getting attached intent data
        email = i.getStringExtra("data");
        Log.i("MyActivity1", email);

        btn = (Button) findViewById(R.id.button);
        eT = (EditText) findViewById(R.id.editText);
        eT2 = (EditText) findViewById(R.id.editText2);
        // show location button click event
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                name = eT.getText().toString().trim();
                contact = eT2.getText().toString().trim();
                String MobilePattern = "[0-9]{10}";
                if (name.equals("")) {
                    Toast.makeText(Details_Activity.this,
                            "Name Field is empty", Toast.LENGTH_SHORT).show();
                } else if (!contact.toString().matches(MobilePattern)) {
                    Toast.makeText(Details_Activity.this,
                            "Invalid Mobile number", Toast.LENGTH_SHORT).show();
                } else
                // true or false
                {
                    savePreferences("saved_id", eT.getText().toString());
                    savePreferences("saved_contact", eT2.getText().toString());
                    savePreferences("CheckBox_Value", true);
                    json_string = email;
                    Intent i = new Intent(getApplicationContext(),
                            Location_Activity.class);
                    // sending data to new activity
                    i.putExtra("data", json_string);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}