package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

import java.text.DateFormat;
import java.util.Calendar;

import static com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry.NO_VALUE;
import static com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry.YES_VALUE;

public class MainMenuActivity extends AppCompatActivity {

    private LinearLayout personalProfileButton;
    private LinearLayout personHealthRecommendationsButton;
    private LinearLayout checklistButton;
    private LinearLayout bloodTestButton;
    private LinearLayout DoctorRecordsButton;
    private Intent personHealthRecommendationsIntent;
    public static boolean animateButtons = false;

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
        if(animateButtons)
            animateButtons();
        OverallNotificationManager.setUpNotificationTimers(this,OverallNotificationManager.NO_ADDITIONAL_ID);
        if(!GeneralPurposeService.isServiceRunning())
            this.startService(new Intent(this,GeneralPurposeService.class));
        animateButtons = false;
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    private void animateButtons() {
        Animation leftToRight = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_left_to_right);
        Animation rightToLeft = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_right_to_left);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.main_menu_button_zoom_in);
        personalProfileButton.startAnimation(zoomIn);
        personHealthRecommendationsButton.startAnimation(rightToLeft);
        checklistButton.startAnimation(rightToLeft);
        DoctorRecordsButton.startAnimation(leftToRight);
        bloodTestButton.startAnimation(leftToRight);
    }

    public void changeActivity(View view) {
        Intent transfer = null;
        if(view.equals(personalProfileButton))
            transfer = new Intent(this, PersonalProfileActivity.class);
        if(view.equals(checklistButton))
            transfer = new Intent(this, ChecklistActivity.class);
        if(view.equals(DoctorRecordsButton))
            transfer = new Intent(this,RecordsActivity.class);
        if(view.equals(bloodTestButton))
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
        int bmi_w = -1;
        int bmi_h = -1;
        try {bmi_w = Integer.parseInt(profile.findEntryByName(getResources().getString(R.string.weight)).getValue());}
        catch(Exception ignore){missing += "- "+res.getString(R.string.weight)+"\n";}
        try {bmi_h = Integer.parseInt(profile.findEntryByName(getResources().getString(R.string.height)).getValue());}
        catch(Exception ignore){missing += "- "+res.getString(R.string.height)+"\n";}
        String pseudoAge = profile.findEntryByName(getResources().getString(R.string.birth_date)).getValue();
        int age = calculateAge(pseudoAge);
        missing += (age == -1) ? "- "+res.getString(R.string.birth_date)+"\n" : "";
        if(missing.length() != 2 || smoke == null || history == null || bmi_w == -1 || bmi_h == -1 || age == -1)
        {
            builder.setMessage(res.getText(R.string.missing_param_for_health_recommendations)+missing);
            builder.setNeutralButton(res.getText(R.string.understood),null).create().show();
        }
        else {
            personHealthRecommendationsIntent = new Intent(this, HealthRecommendationsActivity.class);
            personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_SMOKE,(smoke.equals(YES_VALUE)) ? "yes" : "no");
            switch (history) {
                case YES_VALUE:
                    personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY, "yes");
                    break;
                case NO_VALUE:
                    personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY, "no");
                    break;
                default:
                    personHealthRecommendationsIntent.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY, "dont");
                    break;
            }
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
            int age = currentDay.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (birthDate.get(Calendar.MONTH) > currentDay.get(Calendar.MONTH)
                    || (birthDate.get(Calendar.MONTH) == currentDay.get(Calendar.MONTH)
                    && birthDate.get(Calendar.DATE) > currentDay.get(Calendar.DATE)))
                age--;
            return age;
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
                prefs.edit().putBoolean("personal_health_recommendations_disclaimer_approved", true).apply();
                startActivity(personHealthRecommendationsIntent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
