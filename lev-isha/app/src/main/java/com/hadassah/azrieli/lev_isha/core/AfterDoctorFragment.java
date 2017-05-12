package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.hadassah.azrieli.lev_isha.R;

public class AfterDoctorFragment extends Fragment {

    private EditText summaryOfDoctorsRemarks;
    private CheckBox determineYourRiskOfHeartDisease;
    private CheckBox askAboutFutureTreatment;
    private CheckBox shouldYouComeBack;
    private SharedPreferences prefs;
    private boolean alreadyUpdated = false;

    public AfterDoctorFragment() {}

    public static AfterDoctorFragment newInstance() {
        AfterDoctorFragment fragment = new AfterDoctorFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_after_doctor, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View parent = getView();
        CheckBoxChanged cbcLis = new CheckBoxChanged();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        summaryOfDoctorsRemarks = (EditText)parent.findViewById(R.id.text_box_summary_of_the_doctors_Remarks);
        determineYourRiskOfHeartDisease = (CheckBox)parent.findViewById(R.id.check_box_determine_your_risk_of_heart_disease);
        askAboutFutureTreatment = (CheckBox)parent.findViewById(R.id.check_box_ask_about_future_treatment);
        shouldYouComeBack = (CheckBox)parent.findViewById(R.id.check_box_should_you_come_back_for_further_inspections);
        summaryOfDoctorsRemarks.setTag("summary_of_the_doctors_Remarks");
        determineYourRiskOfHeartDisease.setTag("determine_your_risk_of_heart_disease");
        askAboutFutureTreatment.setTag("ask_about_future_treatment");
        shouldYouComeBack.setTag("should_you_come_back_for_further_inspections");
        summaryOfDoctorsRemarks.addTextChangedListener(new TextChangedListener(getActivity(),summaryOfDoctorsRemarks));
        determineYourRiskOfHeartDisease.setOnCheckedChangeListener(cbcLis);
        askAboutFutureTreatment.setOnCheckedChangeListener(cbcLis);
        shouldYouComeBack.setOnCheckedChangeListener(cbcLis);
    }


    public void reloadSavedFields() {
        if(alreadyUpdated)
            return;
        alreadyUpdated = true;
        summaryOfDoctorsRemarks.setText(prefs.getString((String)summaryOfDoctorsRemarks.getTag(),""));
        determineYourRiskOfHeartDisease.setChecked(prefs.getBoolean((String)determineYourRiskOfHeartDisease.getTag(),false));
        askAboutFutureTreatment.setChecked(prefs.getBoolean((String)askAboutFutureTreatment.getTag(),false));
        shouldYouComeBack.setChecked(prefs.getBoolean((String)shouldYouComeBack.getTag(),false));
    }

    public void onPause() {
        super.onPause();
        alreadyUpdated = false;
    }

    private class CheckBoxChanged implements CompoundButton.OnCheckedChangeListener {

        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            prefs.edit().putBoolean((String)compoundButton.getTag(),b).apply();
        }
    }

    private class TextChangedListener extends AbsListView {

        EditText textField;

        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            prefs.edit().putString((String)textField.getTag(),s.toString()).apply();
        }

        public TextChangedListener(Context context, EditText field) {
            super(context);
            textField = field;
        }

        public ListAdapter getAdapter() {return null;}

        public void setSelection(int i) {}
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onDetach() {
        super.onDetach();
    }

}
