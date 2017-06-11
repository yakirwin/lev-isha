package com.hadassah.azrieli.lev_isha.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TimePicker;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BeforeDoctorFragment extends Fragment {

    private SharedPreferences prefs;
    private boolean alreadyUpdated = false;
    private EditText doctorName;
    private EditText address;
    private EditText date;
    private EditText time;
    private Button saveAppointment;
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
        return new BeforeDoctorFragment();
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

    private class ChooserListener implements View.OnFocusChangeListener, View.OnClickListener {
        public void onFocusChange(View view, boolean b) {
            if(!b)
                return;
            if(view.equals(date))
                openDateChooser();
            if(view.equals(time))
                openTimeChooser();
        }
        public void onClick(View view) {
            if(view.equals(date))
                openDateChooser();
            if(view.equals(time))
                openTimeChooser();
        }
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
        doctorName = parent.findViewById(R.id.edit_text_doctor_name);
        address = parent.findViewById(R.id.edit_text_address);
        date = parent.findViewById(R.id.appointment_date);
        time = parent.findViewById(R.id.appointment_time);
        saveAppointment = parent.findViewById(R.id.add_appointment_to_calendar);
        saveAppointment.setEnabled(false);
        references = parent.findViewById(R.id.check_box_references);
        prescription = parent.findViewById(R.id.check_box_prescription);
        previousDiagnoses = parent.findViewById(R.id.check_box_Previous_diagnoses);
        testsResults = parent.findViewById(R.id.check_box_tests_results);
        subjectsIWantToTalkAbout = parent.findViewById(R.id.text_box_subjects_i_want_to_talk_about);
        newSymptoms = parent.findViewById(R.id.text_box_new_symptoms);
        changesInLife = parent.findViewById(R.id.text_box_changes_in_life);
        personalMedicalHistory = parent.findViewById(R.id.text_box_personal_medical_history);
        familyHealthBackground = parent.findViewById(R.id.text_box_family_health_background);
        drugsITake = parent.findViewById(R.id.text_box_drugs_i_take);
        additionalQuestionsToTheDoctor = parent.findViewById(R.id.text_box_additional_questions_to_the_doctor);
    }

    private void setTags() {
        doctorName.setTag("edit_text_doctor_name");
        address.setTag("edit_text_address");
        date.setTag("appointment_date");
        time.setTag("appointment_time");
        saveAppointment.setTag("add_appointment_to_calendar");
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
        ChooserListener chooserLis = new ChooserListener();
        doctorName.addTextChangedListener(new TextChangedListener(parentActivity,doctorName));
        address.addTextChangedListener(new TextChangedListener(parentActivity,address));
        date.setOnFocusChangeListener(chooserLis);
        date.setOnClickListener(chooserLis);
        time.setOnFocusChangeListener(chooserLis);
        time.setOnClickListener(chooserLis);
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
        saveAppointment.setOnClickListener(new View.OnClickListener()
        {public void onClick(View view) {addEventToCalendar();}});
    }

    public void addEventToCalendar() {
        Calendar dueDate = getCalendarObjectFromFields();
        if(dueDate == null)
            return;
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT);
        calendarIntent.setData(CalendarContract.Events.CONTENT_URI);
        calendarIntent.setType("vnd.android.cursor.item/event");
        calendarIntent.putExtra(CalendarContract.Events.TITLE,getResources().getString(R.string.calendar_reminder_header));
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,dueDate.getTimeInMillis());
        dueDate.add(Calendar.HOUR,1);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dueDate.getTimeInMillis());
        startActivity(calendarIntent);
    }

    public void openDateChooser() {
        int year, month, day;
        Calendar currentTime = Calendar.getInstance();
        final DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,  PersonalProfile.getCurrentLocale());
        Calendar inMemory = Calendar.getInstance();
        try {inMemory.setTime(df.parse(date.getText().toString()));}catch(Exception ignore){inMemory = null;}
        year = (inMemory == null) ? currentTime.get(Calendar.YEAR) : inMemory.get(Calendar.YEAR);
        month = (inMemory == null) ? currentTime.get(Calendar.MONTH) : inMemory.get(Calendar.MONTH);
        day = (inMemory == null) ? currentTime.get(Calendar.DAY_OF_MONTH) : inMemory.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(i,i1,i2);
                String display = df.format(calendar.getTime());
                date.setText(display);
                prefs.edit().putString((String)date.getTag(),display).apply();
                shouldEnableAppointmentButton();
            }
        }, year, month, day).show();
    }

    @SuppressLint("SimpleDateFormat")
    public void openTimeChooser() {
        int hour, min;
        final Calendar currentTime = Calendar.getInstance();
        //final DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        final DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar inMemory = Calendar.getInstance();
        try {inMemory.setTime(df.parse(time.getText().toString()));} catch(Exception ignore){inMemory = null;}
        hour = (inMemory == null) ? currentTime.get(Calendar.HOUR_OF_DAY) : inMemory.get(Calendar.HOUR_OF_DAY);
        min = (inMemory == null) ? currentTime.get(Calendar.MINUTE) : inMemory.get(Calendar.MINUTE);
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Calendar calendar = Calendar.getInstance();
                int year = currentTime.get(Calendar.YEAR);
                int month = currentTime.get(Calendar.MONTH);
                int day = currentTime.get(Calendar.DAY_OF_MONTH);
                calendar.set(year,month,day,i,i1);
                String display = df.format(calendar.getTime());
                time.setText(display);
                prefs.edit().putString((String)time.getTag(),display).apply();
                shouldEnableAppointmentButton();
            }
        },hour,min,true).show();
    }


    public void reloadSavedFields() {
        if(alreadyUpdated)
            return;
        alreadyUpdated = true;
        doctorName.setText(prefs.getString((String)doctorName.getTag(),""));
        address.setText(prefs.getString((String)address.getTag(),""));
        date.setText(prefs.getString((String)date.getTag(),""));
        time.setText(prefs.getString((String)time.getTag(),""));
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
        shouldEnableAppointmentButton();
    }


    @SuppressLint("ApplySharedPref")
    public void shouldEnableAppointmentButton() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(date.getText().length() != 0 && time.getText().length() != 0)
        {
            Calendar dueDate = getCalendarObjectFromFields();
            if(dueDate == null)
                return;
            saveAppointment.setEnabled(true);
            long inMemory = prefs.getLong("next_doctor_appointment_date_in_ms",-1);
            if(inMemory == getCalendarObjectFromFields().getTimeInMillis())
                return;
            if(inMemory != -1)
                OverallNotificationManager.cancelDoctorAppointment(getActivity());
            prefs.edit().putLong("next_doctor_appointment_date_in_ms",dueDate.getTimeInMillis()).commit();
            OverallNotificationManager.setUpNotificationTimers(getActivity(),OverallNotificationManager.NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID);
            OverallNotificationManager.setUpNotificationTimers(getActivity(),OverallNotificationManager.NOTIFICATION_DAY_BEFORE_DOCTOR_ID);
        }
        else
        {
            saveAppointment.setEnabled(false);
            if(prefs.getLong("next_doctor_appointment_date_in_ms",-1) != -1)
            {
                OverallNotificationManager.cancelDoctorAppointment(getActivity());
                prefs.edit().putLong("next_doctor_appointment_date_in_ms",-1).commit();
            }
        }
    }

    public Calendar getCalendarObjectFromFields() {
        DateFormat dfDate = DateFormat.getDateInstance(DateFormat.DEFAULT, PersonalProfile.getCurrentLocale());
        @SuppressLint("SimpleDateFormat")
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        //DateFormat dfTime = DateFormat.getTimeInstance(DateFormat.SHORT);
        Calendar timeCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        try{timeCal.setTime(dfTime.parse(time.getText().toString()));} catch(Exception ignore){return null;}
        try{dateCal.setTime(dfDate.parse(date.getText().toString()));} catch(Exception ignore){return null;}
        int year = dateCal.get(Calendar.YEAR);
        int month = dateCal.get(Calendar.MONTH);
        int day = dateCal.get(Calendar.DAY_OF_MONTH);
        int hour = timeCal.get(Calendar.HOUR_OF_DAY);
        int min = timeCal.get(Calendar.MINUTE);
        dueDate.set(year, month, day, hour, min);
        dueDate.set(Calendar.SECOND, dueDate.getActualMinimum(Calendar.SECOND));
        dueDate.set(Calendar.MILLISECOND, dueDate.getActualMinimum(Calendar.MILLISECOND));
        return dueDate;
    }

    public void clearForm() {
        prefs.edit().putString((String)doctorName.getTag(),"").apply();
        prefs.edit().putString((String)address.getTag(),"").apply();
        prefs.edit().putString((String)date.getTag(),"").apply();
        prefs.edit().putString((String)time.getTag(),"").apply();
        prefs.edit().putBoolean((String)references.getTag(),false).apply();
        prefs.edit().putBoolean((String)prescription.getTag(),false).apply();
        prefs.edit().putBoolean((String)previousDiagnoses.getTag(),false).apply();
        prefs.edit().putBoolean((String)testsResults.getTag(),false).apply();
        prefs.edit().putString((String)subjectsIWantToTalkAbout.getTag(),"").apply();
        prefs.edit().putString((String)newSymptoms.getTag(),"").apply();
        prefs.edit().putString((String)changesInLife.getTag(),"").apply();
        prefs.edit().putString((String)personalMedicalHistory.getTag(),"").apply();
        prefs.edit().putString((String)familyHealthBackground.getTag(),"").apply();
        prefs.edit().putString((String)drugsITake.getTag(),"").apply();
        prefs.edit().putString((String)additionalQuestionsToTheDoctor.getTag(),"").apply();
        doctorName.setText("");
        address.setText("");
        date.setText("");
        time.setText("");
        references.setSelected(false);
        prescription.setSelected(false);
        previousDiagnoses.setSelected(false);
        testsResults.setSelected(false);
        subjectsIWantToTalkAbout.setText("");
        newSymptoms.setText("");
        changesInLife.setText("");
        personalMedicalHistory.setText("");
        familyHealthBackground.setText("");
        drugsITake.setText("");
        additionalQuestionsToTheDoctor.setText("");
        alreadyUpdated = false;
        reloadSavedFields();
    }


}
