package com.hadassah.azrieli.lev_isha.core;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.hadassah.azrieli.lev_isha.R;

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

    public void setupReminder(View view) {

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
