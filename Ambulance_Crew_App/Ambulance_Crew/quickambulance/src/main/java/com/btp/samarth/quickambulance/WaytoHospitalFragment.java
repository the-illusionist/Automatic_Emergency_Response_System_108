package com.btp.samarth.quickambulance;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WaytoHospitalFragment extends Fragment {
	
	public WaytoHospitalFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_hospital, container, false);
         
        return rootView;
    }
}
