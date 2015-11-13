package com.btp.samarth.quickambulance;

import android.app.Application;
import android.content.Context;

/**
 * Created by Abhi on 12-11-2015.
 */
public class Variables extends Application {
    String amb_regId;
    String amb_lati;
    String amb_longi;
    String user_name;
    String user_contact;
    String user_lati;
    String user_longi;
    private static Context myContext;

    public void onCreate(){
        super.onCreate();
        this.myContext = this;
    }

    public static Context getContext(){
        return myContext;
    }

    public String getamb_regId() {
        return amb_regId;
    }

    public void Setamb_regId(String someVariable) {
        this.amb_regId = someVariable;
    }

    public String getamb_lati() {
        return amb_lati;
    }

    public void Setamb_lati(String someVariable) {
        this.amb_lati = someVariable;
    }

    public String getamb_longi() {
        return amb_longi;
    }

    public void Setamb_longi(String someVariable) {
        this.amb_longi = someVariable;
    }
}
