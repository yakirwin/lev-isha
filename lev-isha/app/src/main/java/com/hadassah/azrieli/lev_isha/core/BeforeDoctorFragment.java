package com.hadassah.azrieli.lev_isha.core;

import android.app.Activity;
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

public class BeforeDoctorFragment extends Fragment {

    private SharedPreferences prefs;
    private boolean alreadyUpdated = false;
    private EditText doctorName;
    private EditText address;
    private EditText appointmentDate;
    private EditText appointmentHour;
    private CheckBox references;
    private CheckBox prescription;
    private CheckBox previousDiagnoses;
    private CheckBox testsResults;
    private EditText subjectsIWantToTalkAbout;
    private EditText newSymptoms;
    private EditText changesInLife;
    private EditText personalMedicalHistory;
    private EditText familyHealthBackground;
    private EditText drugsITake;
    private EditText additionalQuestionsToTheDoctor;

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

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        bindViews(getView());
        setTags();
        bindListeners();
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

    public void onPause() {
        super.onPause();
        alreadyUpdated = false;
    }

    private void bindViews(View parent) {
        doctorName = (EditText)parent.findViewById(R.id.edit_text_doctor_name);
        address = (EditText)parent.findViewById(R.id.edit_text_address);
        appointmentDate = (EditText)parent.findViewById(R.id.edit_text_appointment_date);
        appointmentHour = (EditText)parent.findViewById(R.id.edit_text_appointment_hour);
        references = (CheckBox)parent.findViewById(R.id.check_box_references);
        prescription = (CheckBox)parent.findViewById(R.id.check_box_prescription);
        previousDiagnoses = (CheckBox)parent.findViewById(R.id.check_box_Previous_diagnoses);
        testsResults = (CheckBox)parent.findViewById(R.id.check_box_tests_results);
        subjectsIWantToTalkAbout = (EditText)parent.findViewById(R.id.text_box_subjects_i_want_to_talk_about);
        newSymptoms = (EditText)parent.findViewById(R.id.text_box_new_symptoms);
        changesInLife = (EditText)parent.findViewById(R.id.text_box_changes_in_life);
        personalMedicalHistory = (EditText)parent.findViewById(R.id.text_box_personal_medical_history);
        familyHealthBackground = (EditText)parent.findViewById(R.id.text_box_family_health_background);
        drugsITake = (EditText)parent.findViewById(R.id.text_box_drugs_i_take);
        additionalQuestionsToTheDoctor = (EditText)parent.findViewById(R.id.text_box_additional_questions_to_the_doctor);
    }

    private void setTags() {
        doctorName.setTag("edit_text_doctor_name");
        address.setTag("edit_text_address");
        appointmentDate.setTag("edit_text_appointment_date");
        appointmentHour.setTag("edit_text_appointment_hour");
        references.setTag("check_box_references");
        prescription.setTag("check_box_prescription");
        previousDiagnoses.setTag("check_box_Previous_diagnoses");
        testsResults.setTag("check_box_tests_results");
        subjectsIWantToTalkAbout.setTag("text_box_subjects_i_want_to_talk_about");
        newSymptoms.setTag("text_box_new_symptoms");
        changesInLife.setTag("text_box_changes_in_life");
        personalMedicalHistory.setTag("text_box_personal_medical_history");
        familyHealthBackground.setTag("text_box_family_health_background");
        drugsITake.setTag("text_box_drugs_i_take");
        additionalQuestionsToTheDoctor.setTag("text_box_additional_questions_to_the_doctor");
    }

    private void bindListeners() {
        Activity parentActivity = getActivity();
        CheckBoxChanged cbcLis = new CheckBoxChanged();
        doctorName.addTextChangedListener(new TextChangedListener(parentActivity,doctorName));
        address.addTextChangedListener(new TextChangedListener(parentActivity,address));
        appointmentDate.addTextChangedListener(new TextChangedListener(parentActivity,appointmentDate));
        appointmentHour.addTextChangedListener(new TextChangedListener(parentActivity,appointmentHour));
        references.setOnCheckedChangeListener(cbcLis);
        prescription.setOnCheckedChangeListener(cbcLis);
        previousDiagnoses.setOnCheckedChangeListener(cbcLis);
        testsResults.setOnCheckedChangeListener(cbcLis);
        subjectsIWantToTalkAbout.addTextChangedListener(new TextChangedListener(parentActivity,subjectsIWantToTalkAbout));
        newSymptoms.addTextChangedListener(new TextChangedListener(parentActivity,newSymptoms));
        changesInLife.addTextChangedListener(new TextChangedListener(parentActivity,changesInLife));
        personalMedicalHistory.addTextChangedListener(new TextChangedListener(parentActivity,personalMedicalHistory));
        familyHealthBackground.addTextChangedListener(new TextChangedListener(parentActivity,familyHealthBackground));
        drugsITake.addTextChangedListener(new TextChangedListener(parentActivity,drugsITake));
        additionalQuestionsToTheDoctor.addTextChangedListener(new TextChangedListener(parentActivity,additionalQuestionsToTheDoctor));
    }

    public void reloadSavedFields() {
        if(alreadyUpdated)
            return;
        alreadyUpdated = true;
        doctorName.setText(prefs.getString((String)doctorName.getTag(),""));
        address.setText(prefs.getString((String)address.getTag(),""));
        appointmentDate.setText(prefs.getString((String)appointmentDate.getTag(),""));
        appointmentHour.setText(prefs.getString((String)appointmentHour.getTag(),""));
        references.setChecked(prefs.getBoolean((String)references.getTag(),false));
        prescription.setChecked(prefs.getBoolean((String)prescription.getTag(),false));
        previousDiagnoses.setChecked(prefs.getBoolean((String)previousDiagnoses.getTag(),false));
        testsResults.setChecked(prefs.getBoolean((String)testsResults.getTag(),false));
        subjectsIWantToTalkAbout.setText(prefs.getString((String)subjectsIWantToTalkAbout.getTag(),""));
        newSymptoms.setText(prefs.getString((String)newSymptoms.getTag(),""));
        changesInLife.setText(prefs.getString((String)changesInLife.getTag(),""));
        personalMedicalHistory.setText(prefs.getString((String)personalMedicalHistory.getTag(),""));
        familyHealthBackground.setText(prefs.getString((String)familyHealthBackground.getTag(),""));
        drugsITake.setText(prefs.getString((String)drugsITake.getTag(),""));
        additionalQuestionsToTheDoctor.setText(prefs.getString((String)additionalQuestionsToTheDoctor.getTag(),""));
    }

}
