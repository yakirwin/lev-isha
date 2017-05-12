package com.hadassah.azrieli.lev_isha.core;

import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry.NO_VALUE;
import static com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry.YES_VALUE;

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class MainMenuActivity extends AppCompatActivity {

    private Button personalProfileButton;
    private Button personHealthRecommendationsButton;
    private Button setReminderButton;
    private Button checklistButton;
    private Intent personHealthRecommendationsIntent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        personalProfileButton = (Button)findViewById(R.id.personal_profile_btn);
        personHealthRecommendationsButton = (Button)findViewById(R.id.personal_health_recommendation_btn);
        setReminderButton = (Button)findViewById(R.id.set_reminder_btn);
        checklistButton = (Button)findViewById(R.id.checklist_btn);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(prefs.getBoolean("show_question_to_setup_profile",true)) {
            prefs.edit().putBoolean("show_question_to_setup_profile", false).apply();
            showMessageToSetupProfile();
        }
        animateButtons();
    }

    private void animateButtons() {
        Animation leftToRight = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_left_to_right);
        Animation rightToLeft = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_right_to_left);
        personalProfileButton.startAnimation(leftToRight);
        personHealthRecommendationsButton.startAnimation(rightToLeft);
        setReminderButton.startAnimation(leftToRight);
        checklistButton.startAnimation(rightToLeft);
    }

    public void changeActivity(View view) {
        Intent transfer = null;
        if(view == personalProfileButton)
            transfer = new Intent(this, PersonalProfileActivity.class);
        if(view == checklistButton)
            transfer = new Intent(this, ChecklistActivity.class);
        if(transfer != null)
            startActivity(transfer);
    }

    public void personHealthRecommendations(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PersonalProfile profile = PersonalProfile.getInstance(this);
        String missing = "\n\n";
        Resources res = getResources();
        String smoke = profile.findEntryByName(res.getString(R.string.smoking)).getValue();
        missing += (smoke == null) ? "-"+res.getString(R.string.smoking)+"\n" : "";
        String history = profile.findEntryByName(res.getString(R.string.family_history_personal_profile)).getValue();
        missing += (history == null) ? "-"+res.getString(R.string.family_history_personal_profile)+"\n" : "";
        int bmi_w = -1, bmi_h = -1;
        try {
            bmi_w = Integer.parseInt(profile.findEntryByName(getResources().getString(R.string.weight)).getValue());
        }catch(Exception ignore){
            missing += "-"+res.getString(R.string.weight)+"\n";
        }
        try {
            bmi_h = Integer.parseInt(profile.findEntryByName(getResources().getString(R.string.height)).getValue());
        }catch(Exception ignore){
            missing += "-"+res.getString(R.string.height)+"\n";
        }
        String pseudoAge = profile.findEntryByName(getResources().getString(R.string.birth_date)).getValue();
        int age = calculateAge(pseudoAge);
        missing += (age == -1) ? "-"+res.getString(R.string.birth_date)+"\n" : "";
        if(missing.length() != 2)
        {
            builder.setMessage(res.getText(R.string.missing_param_for_health_recommendations)+missing);
            builder.setNeutralButton(res.getText(R.string.understood),null).create().show();
        }
        else {
            personHealthRecommendationsIntent = new Intent(this, HealthRecommendationsActivity.class);
            personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_SMOKE,(smoke.equals(YES_VALUE)) ? "yes" : "no");
            if(smoke.equals(YES_VALUE))
                personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY,"yes");
            else if(smoke.equals(NO_VALUE))
                personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY,"no");
            else
                personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY,"dont");
            personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HEIGHT, bmi_h);
            personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_WEIGHT, bmi_w);
            personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_AGE,age);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(!prefs.getBoolean("personal_health_recommendations_disclaimer_approved", false))
                askUserForHisConsent();
            else
                startActivity(personHealthRecommendationsIntent);
        }
    }

    private int calculateAge(String format) {
        if(format == null)
            return -1;
        try {
            DateFormat df = DateFormat.getDateInstance();
            Calendar birthDate = Calendar.getInstance();
            Calendar currentDay = Calendar.getInstance();
            birthDate.setTime(df.parse(format));
            int diff = currentDay.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (birthDate.get(Calendar.MONTH) > currentDay.get(Calendar.MONTH)
                    || (birthDate.get(Calendar.MONTH) == currentDay.get(Calendar.MONTH)
                    && birthDate.get(Calendar.DATE) > currentDay.get(Calendar.DATE)))
                diff--;
            return diff;
        }catch(Exception ignore){return -1;}
    }

    public void setupReminder(View view) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR), month = now.get(Calendar.MONTH), day = now.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar dueDate = Calendar.getInstance();
                dueDate.set(i,i1,i2);
                dueDate.set(Calendar.HOUR_OF_DAY, dueDate.getActualMinimum(Calendar.HOUR_OF_DAY));
                dueDate.set(Calendar.MINUTE, dueDate.getActualMinimum(Calendar.MINUTE));
                dueDate.set(Calendar.SECOND, dueDate.getActualMinimum(Calendar.SECOND));
                dueDate.set(Calendar.MILLISECOND, dueDate.getActualMinimum(Calendar.MILLISECOND));
                Intent calendarIntent = new Intent(Intent.ACTION_INSERT);
                calendarIntent.setData(CalendarContract.Events.CONTENT_URI);
                calendarIntent.setType("vnd.android.cursor.item/event");
                calendarIntent.putExtra(CalendarContract.Events.TITLE,getResources().getString(R.string.calendar_reminder_header));
                calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,dueDate.getTimeInMillis());
                calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dueDate.getTimeInMillis());
                startActivity(calendarIntent);
            }
        }, year, month, day).show();
    }

    private void showMessageToSetupProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.welcome));
        builder.setMessage(getResources().getText(R.string.setup_profile_initial_question));
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(MainMenuActivity.this, PersonalProfileActivity.class));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void askUserForHisConsent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.personal_health_recommendations_disclaimer_header));
        builder.setMessage(getResources().getText(R.string.personal_health_recommendations_disclaimer_body));
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putBoolean("personal_health_recommendations_disclaimer_approved", true).commit();
                startActivity(personHealthRecommendationsIntent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
