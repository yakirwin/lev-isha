package com.hadassah.azrieli.lev_isha.core;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.NotificationPublisher;
import com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry;
import com.hadassah.azrieli.lev_isha.utility.VoiceRecorder;

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

    private LinearLayout personalProfileButton;
    private LinearLayout personHealthRecommendationsButton;
    private LinearLayout checklistButton;
    private LinearLayout bloodTestButton;
    private LinearLayout DoctorRecordsButton;
    private Intent personHealthRecommendationsIntent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        personalProfileButton = (LinearLayout)findViewById(R.id.personal_profile_btn);
        personHealthRecommendationsButton = (LinearLayout)findViewById(R.id.personal_health_recommendation_btn);
        checklistButton = (LinearLayout)findViewById(R.id.checklist_btn);
        bloodTestButton = (LinearLayout)findViewById(R.id.blood_result_btn);
        DoctorRecordsButton = (LinearLayout)findViewById(R.id.doctor_records_btn);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(prefs.getBoolean("show_question_to_setup_profile",true)) {
            prefs.edit().putBoolean("show_question_to_setup_profile", false).apply();
            showMessageToSetupProfile();
        }
        animateButtons();
        OverallNotificationManager.setUpNotificationTimers(this,OverallNotificationManager.NO_ADDITIONAL_ID);
        this.startService(new Intent(this,GeneralPurposeService.class));
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    private void animateButtons() {
        Animation leftToRight = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_left_to_right);
        Animation rightToLeft = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_right_to_left);
        personalProfileButton.startAnimation(leftToRight);
        personHealthRecommendationsButton.startAnimation(rightToLeft);
        checklistButton.startAnimation(rightToLeft);
        DoctorRecordsButton.startAnimation(leftToRight);
        bloodTestButton.startAnimation(rightToLeft);
    }

    public void changeActivity(View view) {
        Intent transfer = null;
        if(view == personalProfileButton)
            transfer = new Intent(this, PersonalProfileActivity.class);
        if(view == checklistButton)
            transfer = new Intent(this, ChecklistActivity.class);
        if(view == DoctorRecordsButton)
            transfer = new Intent(this,RecordsActivity.class);
        if(view == bloodTestButton)
            transfer = new Intent(this,BloodTestActivity.class);
        if(transfer != null)
            startActivity(transfer);
    }

    public void personHealthRecommendations(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PersonalProfile profile = PersonalProfile.getInstance(this);
        String missing = "\n\n";
        Resources res = getResources();
        String smoke = profile.findEntryByName(res.getString(R.string.smoking)).getValue();
        missing += (smoke == null) ? "- "+res.getString(R.string.smoking)+"\n" : "";
        String history = profile.findEntryByName(res.getString(R.string.family_history_personal_profile)).getValue();
        missing += (history == null) ? "- "+res.getString(R.string.family_history_personal_profile)+"\n" : "";
        int bmi_w = -1, bmi_h = -1;
        try {bmi_w = Integer.parseInt(profile.findEntryByName(getResources().getString(R.string.weight)).getValue());}
        catch(Exception ignore){missing += "- "+res.getString(R.string.weight)+"\n";}
        try {bmi_h = Integer.parseInt(profile.findEntryByName(getResources().getString(R.string.height)).getValue());}
        catch(Exception ignore){missing += "- "+res.getString(R.string.height)+"\n";}
        String pseudoAge = profile.findEntryByName(getResources().getString(R.string.birth_date)).getValue();
        int age = calculateAge(pseudoAge);
        missing += (age == -1) ? "- "+res.getString(R.string.birth_date)+"\n" : "";
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
            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,  PersonalProfile.getCurrentLocale());
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
