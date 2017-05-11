package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hadassah.azrieli.lev_isha.R;

public class BeforeDoctorFragment extends Fragment {

    public static BeforeDoctorFragment newInstance() {
        BeforeDoctorFragment fragment = new BeforeDoctorFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_before_doctor, container, false);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onDetach() {
        super.onDetach();
    }

}
