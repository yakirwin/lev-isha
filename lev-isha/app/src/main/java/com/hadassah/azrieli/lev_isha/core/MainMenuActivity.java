package com.hadassah.azrieli.lev_isha.core;

import android.content.Intent;
import android.provider.CalendarContract;
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



}
