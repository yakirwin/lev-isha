package com.hadassah.azrieli.lev_isha.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.VoiceRecorder;

public class AfterDoctorFragment extends Fragment {

    private EditText summaryOfDoctorsRemarks;
    private CheckBox determineYourRiskOfHeartDisease;
    private CheckBox askAboutFutureTreatment;
    private CheckBox shouldYouComeBack;
    private SharedPreferences prefs;
    private boolean alreadyUpdated = false;
    private boolean isRecording = false;
    private int hrs = 0, min = 0, sec = 0;
    private TextView timeStampToUpdate;
    private UpdateTimer updateTimer;

    public AfterDoctorFragment() {}

    public static AfterDoctorFragment newInstance() {
        return new AfterDoctorFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_after_doctor, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View parent = getView();
        CheckBoxChanged cbcLis = new CheckBoxChanged();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        summaryOfDoctorsRemarks = (EditText)parent.findViewById(R.id.text_box_summary_of_the_doctors_Remarks);
        determineYourRiskOfHeartDisease = (CheckBox)parent.findViewById(R.id.check_box_determine_your_risk_of_heart_disease);
        askAboutFutureTreatment = (CheckBox)parent.findViewById(R.id.check_box_ask_about_future_treatment);
        shouldYouComeBack = (CheckBox)parent.findViewById(R.id.check_box_should_you_come_back_for_further_inspections);
        timeStampToUpdate = (TextView) parent.findViewById(R.id.doctor_record_timer);
        summaryOfDoctorsRemarks.setTag("summary_of_the_doctors_Remarks");
        determineYourRiskOfHeartDisease.setTag("determine_your_risk_of_heart_disease");
        askAboutFutureTreatment.setTag("ask_about_future_treatment");
        shouldYouComeBack.setTag("should_you_come_back_for_further_inspections");
        summaryOfDoctorsRemarks.addTextChangedListener(new TextChangedListener(getActivity(),summaryOfDoctorsRemarks));
        determineYourRiskOfHeartDisease.setOnCheckedChangeListener(cbcLis);
        askAboutFutureTreatment.setOnCheckedChangeListener(cbcLis);
        shouldYouComeBack.setOnCheckedChangeListener(cbcLis);
        final Button record = (Button)parent.findViewById(R.id.record_button);
        record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!VoiceRecorder.toggleRecord(parent.getContext()))
                    return;
                if(record.getText().equals(parent.getResources().getString(R.string.doctor_record_end))) {
                    isRecording = false;
                    updateTimer.interrupt();
                    record.setText(parent.getResources().getString(R.string.doctor_record_start));
                    Toast.makeText(getActivity(),getActivity().getText(R.string.file_successfully_saved), Toast.LENGTH_SHORT).show();
                    record.setEnabled(false);
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {record.setEnabled(true);}},500);
                }
                else {
                    record.setEnabled(false);
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {record.setEnabled(true);}},500);
                    record.setText(parent.getResources().getString(R.string.doctor_record_end));
                    isRecording = true;
                    updateTimer = new UpdateTimer(getActivity());
                    updateTimer.start();
                }
            }
        });
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

    private class UpdateTimer extends Thread {

        Activity parent;

        private UpdateTimer(Activity parent) {
            this.parent = parent;
        }

        public void run() {
            while(isRecording) {
                try{sleep(1000);}catch(InterruptedException ignore){}
                parent.runOnUiThread(new Runnable() {
                    public void run() {
                        raiseByOneSecond();
                    }
                });
            }
            parent.runOnUiThread(new Runnable() {
                public void run() {
                    timeStampToUpdate.setText("00:00:00");
                    hrs = 0;
                    min = 0;
                    sec = 0;
                }
            });
        }
    }

    private void raiseByOneSecond() {
        //timeStampToUpdate.setTextColor((sec % 2 == 0) ? Color.RED : Color.BLACK);
        String hrsStr, minStr, secStr;
        sec++;
        if(sec == 60) {
            sec = 0;
            min++;
        }
        if(min == 60) {
            min = 0;
            hrs++;
        }
        secStr = (sec >= 10) ? ""+sec : "0"+sec;
        minStr = (min >= 10) ? ""+min : "0"+min;
        hrsStr = (hrs >= 10) ? ""+hrs : "0"+hrs;
        timeStampToUpdate.setText(hrsStr+":"+minStr+":"+secStr);
    }

    public void clearForm() {
        prefs.edit().putString((String)summaryOfDoctorsRemarks.getTag(),"").apply();
        prefs.edit().putBoolean((String)determineYourRiskOfHeartDisease.getTag(),false).apply();
        prefs.edit().putBoolean((String)askAboutFutureTreatment.getTag(),false).apply();
        prefs.edit().putBoolean((String)shouldYouComeBack.getTag(),false).apply();
        summaryOfDoctorsRemarks.setText("");
        determineYourRiskOfHeartDisease.setChecked(false);
        askAboutFutureTreatment.setChecked(false);
        shouldYouComeBack.setChecked(false);
        alreadyUpdated = false;
        reloadSavedFields();
    }

}
