package com.hadassah.azrieli.lev_isha.core;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry;

import java.text.DateFormat;
import java.util.Calendar;

import static com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry.NO_VALUE;
import static com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry.YES_VALUE;

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class MainMenuActivity extends AppCompatActivity {

    private Button personalProfileButton;
    private Button checklistButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        personalProfileButton = (Button)findViewById(R.id.personal_profile_btn);
        checklistButton = (Button)findViewById(R.id.checklist_btn);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(prefs.getBoolean("show_question_to_setup_profile",true)) {
            prefs.edit().putBoolean("show_question_to_setup_profile", false).apply();
            showMessageToSetupProfile();
        }
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
            Intent transfer = new Intent(this, HealthRecommendationsActivity.class);
            transfer.putExtra(HealthRecommendationsActivity.EXTRA_SMOKE,(smoke.equals(YES_VALUE)) ? "yes" : "no");
            if(smoke.equals(YES_VALUE))
                transfer.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY,"yes");
            else if(smoke.equals(NO_VALUE))
                transfer.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY,"no");
            else
                transfer.putExtra(HealthRecommendationsActivity.EXTRA_HISTORY,"dont");
            transfer.putExtra(HealthRecommendationsActivity.EXTRA_HEIGHT, bmi_h);
            transfer.putExtra(HealthRecommendationsActivity.EXTRA_WEIGHT, bmi_w);
            transfer.putExtra(HealthRecommendationsActivity.EXTRA_AGE,age);
            startActivity(transfer);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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

}
