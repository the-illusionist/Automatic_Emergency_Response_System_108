package com.btp.samarth.quickambulance;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class QuestionnaireFragment extends Fragment {
    TextView tV1,tV2,tV3,tV4,tV5,tV6,tV7,tV8,tV9,tV10;
    Switch s1,s2,s3,s4,s5,s6,s7,s8,s9,s10;
	Button btn;
    String name,contact,regId;
	public QuestionnaireFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        regId=((MainActivity)getActivity()).regId;
        name=((MainActivity)getActivity()).name;
        contact=((MainActivity)getActivity()).contact;
        View rootView = inflater.inflate(R.layout.activity_questionnaire, container, false);
        btn=(Button)rootView.findViewById(R.id.btn_que);
        s1 = (Switch) rootView.findViewById(R.id.switch1);
        s2 = (Switch) rootView.findViewById(R.id.switch2);
        s3 = (Switch) rootView.findViewById(R.id.switch3);
        s4 = (Switch) rootView.findViewById(R.id.switch4);
        s5 = (Switch) rootView.findViewById(R.id.switch5);
        s6 = (Switch) rootView.findViewById(R.id.switch6);
        s7 = (Switch) rootView.findViewById(R.id.switch7);
        s8 = (Switch) rootView.findViewById(R.id.switch8);
        s9 = (Switch) rootView.findViewById(R.id.switch9);
        s10 = (Switch) rootView.findViewById(R.id.switch10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new HttpAsyncTask().execute("http://172.16.100.139:3002");
            }
        });
        return rootView;
    }

    public String POST(String url) {

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
            jsonObject.accumulate("name", name);
            jsonObject.accumulate("contact", contact);
            jsonObject.accumulate("Que 1", s1.isChecked());
            jsonObject.accumulate("Que 2", s2.isChecked());
            jsonObject.accumulate("Que 3", s3.isChecked());
            jsonObject.accumulate("Que 4", s4.isChecked());
            jsonObject.accumulate("Que 5", s5.isChecked());
            jsonObject.accumulate("Que 6", s6.isChecked());
            jsonObject.accumulate("Que 7", s7.isChecked());
            jsonObject.accumulate("Que 8", s8.isChecked());
            jsonObject.accumulate("Que 9", s9.isChecked());
            jsonObject.accumulate("Que 10", s10.isChecked());

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
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);

            } else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Final result is: ", result);
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

        }
    }
}
