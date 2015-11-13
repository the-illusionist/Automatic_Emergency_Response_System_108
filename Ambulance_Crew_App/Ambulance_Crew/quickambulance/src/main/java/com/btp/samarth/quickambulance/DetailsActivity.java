package com.btp.samarth.quickambulance;

/**
 * Created by Samarth on 05-11-2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
public class DetailsActivity extends Activity{
    AlertDialogManager alert = new AlertDialogManager();
    public static String regId;
    EditText txtName;
    EditText txtPhone;
    EditText txtVehicle;

    // Register button
    Button btnNext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent i = getIntent();
        regId = i.getStringExtra("regId");
        txtName = (EditText) findViewById(R.id.editName);
        txtPhone = (EditText) findViewById(R.id.editPhone);
        txtVehicle = (EditText) findViewById(R.id.editVehicleNo);
        btnNext = (Button) findViewById(R.id.nextbutton);
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Read EditText dat
                String name = txtName.getText().toString();
                String phone = txtPhone.getText().toString();
                String vehicle = txtVehicle.getText().toString();

                // Check if user filled the form
                if(name.trim().length() > 0 && phone.trim().length() > 0 && vehicle.trim().length() > 0){
                    // Launch Main Activity
                    Intent i = new Intent(getApplicationContext(), GPlusActivity.class);

                    // Registering user on our server
                    // Sending registraiton details to GPlusActivity
                    i.putExtra("name", name);
                    i.putExtra("phone", phone);
                    i.putExtra("vehicle", vehicle);
                    i.putExtra("regId", regId);
                    startActivity(i);
                    finish();
                }else{
                    // user doen't filled that data
                    // ask him to fill the form
                    alert.showAlertDialog(DetailsActivity.this, "Registration Error!", "Please enter your details", false);
                }
            }
        });
    }
}
