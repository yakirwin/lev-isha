package com.hadassah.azrieli.lev_isha.core;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;

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
    private int RECORD_PERMISSIONS_ASKING_CODE = 550;

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
        summaryOfDoctorsRemarks = parent.findViewById(R.id.text_box_summary_of_the_doctors_Remarks);
        determineYourRiskOfHeartDisease = parent.findViewById(R.id.check_box_determine_your_risk_of_heart_disease);
        askAboutFutureTreatment = parent.findViewById(R.id.check_box_ask_about_future_treatment);
        shouldYouComeBack = parent.findViewById(R.id.check_box_should_you_come_back_for_further_inspections);
        timeStampToUpdate = parent.findViewById(R.id.doctor_record_timer);
        summaryOfDoctorsRemarks.setTag("summary_of_the_doctors_Remarks");
        determineYourRiskOfHeartDisease.setTag("determine_your_risk_of_heart_disease");
        askAboutFutureTreatment.setTag("ask_about_future_treatment");
        shouldYouComeBack.setTag("should_you_come_back_for_further_inspections");
        summaryOfDoctorsRemarks.addTextChangedListener(new TextChangedListener(getActivity(),summaryOfDoctorsRemarks));
        determineYourRiskOfHeartDisease.setOnCheckedChangeListener(cbcLis);
        askAboutFutureTreatment.setOnCheckedChangeListener(cbcLis);
        shouldYouComeBack.setOnCheckedChangeListener(cbcLis);
        final Button record = parent.findViewById(R.id.record_button);
        record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] missingPrem = getMissingPermissions();
                if(missingPrem.length > 0)
                    requestPermissions(missingPrem,RECORD_PERMISSIONS_ASKING_CODE);
                else
                    pressedRecordButton();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RECORD_PERMISSIONS_ASKING_CODE && permissions.length == grantResults.length)
            pressedRecordButton();
    }

    private String[] getMissingPermissions() {
        ArrayList<String> missing = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            missing.add(Manifest.permission.RECORD_AUDIO);
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            missing.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return missing.toArray(new String[missing.size()]);
    }

    private void pressedRecordButton() {
        final View parent = getView();
        final Button record = (parent != null) ? (Button)parent.findViewById(R.id.record_button) : null;
        if(!VoiceRecorder.toggleRecord(parent.getContext()) || record == null)
            return;
        if(record.getText().equals(parent.getResources().getString(R.string.doctor_record_end))) {
            isRecording = false;
            updateTimer.interrupt();
            record.setText(parent.getResources().getString(R.string.doctor_record_start));
            record.setEnabled(false);
            final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.text_input, null);
            final EditText textBox = dialogView.findViewById(R.id.text_input_text_box);
            textBox.setHint(VoiceRecorder.getLastFileName());
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle(parent.getContext().getString(R.string.enter_name_popup_header));
            builder.setView(dialogView);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    killFileNameAlertDialog(textBox,false);
                }
            });
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    killFileNameAlertDialog(textBox,true);
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    killFileNameAlertDialog(textBox,false);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
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

    private void killFileNameAlertDialog(TextView textBox, boolean canEdit) {
        Toast.makeText(getActivity(),getActivity().getText(R.string.file_successfully_saved), Toast.LENGTH_SHORT).show();
        if(canEdit && textBox.getText() != null && textBox.getText().toString().length() > 0)
            VoiceRecorder.changeName(textBox.getText().toString());
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textBox.getWindowToken(), 0);
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

        private EditText textField;

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

        private Activity parent;

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
        String hrsStr;
        String minStr;
        String secStr;
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
