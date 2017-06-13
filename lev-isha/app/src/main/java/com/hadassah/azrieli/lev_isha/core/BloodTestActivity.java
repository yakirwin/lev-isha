package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

public class BloodTestActivity extends AppCompatActivity {

    private ExpandableRelativeLayout explBMI, explBloodPressure, explCholesterolGeneral,
            explCholesterolLdl, explCholesterolHdl, explTriglyceride,
            explGlucoseFasting, explHbA1C;
    private Button btnLBMI, btnBloodPressure, btnCholesterolGeneral, btnCholesterolLdl,
            btnCholesterolHdl, btnTriglyceride, btnGlucoseFasting, btnHbA1C;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_test);
        bindButtons();
        bindLayouts();
        collapseAll();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(R.string.blood_test_result_guide_label);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(!GeneralPurposeService.isServiceRunning())
            this.startService(new Intent(this,GeneralPurposeService.class));
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blood_test_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.blood_test_options_collapse_all_sections) {
            collapseAll();
            return true;
        }
        if(item.getItemId() == R.id.blood_test_options_expand_all_sections) {
            expandAll();
            return true;
        }
        return false;
    }

    public void pressedExpandableButton(View view) {
        ExpandableRelativeLayout pressed = null;
        if(view.equals(btnLBMI))
            pressed = explBMI;
        else if(view.equals(btnBloodPressure))
            pressed = explBloodPressure;
        else if(view.equals(btnCholesterolGeneral))
            pressed = explCholesterolGeneral;
        else if(view.equals(btnCholesterolLdl))
            pressed = explCholesterolLdl;
        else if(view.equals(btnCholesterolHdl))
            pressed = explCholesterolHdl;
        else if(view.equals(btnTriglyceride))
            pressed = explTriglyceride;
        else if(view.equals(btnGlucoseFasting))
            pressed = explGlucoseFasting;
        else if(view.equals(btnHbA1C))
            pressed = explHbA1C;
        if(pressed == null)
            return;
        pressed.toggle();
    }

    private void bindButtons() {
        btnLBMI = (Button) findViewById(R.id.blood_test_button_bmi);
        btnBloodPressure = (Button) findViewById(R.id.blood_test_button_blood_pressure);
        btnCholesterolGeneral = (Button) findViewById(R.id.blood_test_button_cholesterol_general);
        btnCholesterolLdl = (Button) findViewById(R.id.blood_test_button_cholesterol_ldl);
        btnCholesterolHdl = (Button) findViewById(R.id.blood_test_button_cholesterol_hdl);
        btnTriglyceride = (Button) findViewById(R.id.blood_test_button_triglyceride);
        btnGlucoseFasting = (Button) findViewById(R.id.blood_test_button_glucose_fasting);
        btnHbA1C = (Button) findViewById(R.id.blood_test_button_HbA1C);
    }

    private void bindLayouts() {
        explBMI = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_bmi);
        explBloodPressure = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_blood_pressure);
        explCholesterolGeneral = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_cholesterol_general);
        explCholesterolLdl = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_cholesterol_ldl);
        explCholesterolHdl = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_cholesterol_hdl);
        explTriglyceride = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_triglyceride);
        explGlucoseFasting = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_glucose_fasting);
        explHbA1C = (ExpandableRelativeLayout) findViewById(R.id.blood_test_expl_HbA1C);
    }

    private void collapseAll() {
        explBMI.collapse();
        explBloodPressure.collapse();
        explCholesterolGeneral.collapse();
        explCholesterolLdl.collapse();
        explCholesterolHdl.collapse();
        explTriglyceride.collapse();
        explGlucoseFasting.collapse();
        explHbA1C.collapse();
    }

    private void expandAll() {
        explBMI.expand();
        explBloodPressure.expand();
        explCholesterolGeneral.expand();
        explCholesterolLdl.expand();
        explCholesterolHdl.expand();
        explTriglyceride.expand();
        explGlucoseFasting.expand();
        explHbA1C.expand();
    }

    public void pressedToRead(View view) {
        Intent openWebView = new Intent(this,BloodTestWebViewActivity.class);
        if(view == findViewById(R.id.blood_test_read_here_bmi)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.BMI_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_bmi_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_blood_pressure)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.BLOOD_PRESSURE_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_blood_pressure_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_cholesterol_general)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.CHOLESTEROL_GENERAL_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_cholesterol_general_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_cholesterol_hdl)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.CHOLESTEROL_HDL_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_cholesterol_hdl_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_cholesterol_ldl)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.CHOLESTEROL_LDL_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_cholesterol_ldl_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_glucose_fasting)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.GLUCOSE_FASTING_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_glucose_fasting_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_HbA1C)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.HBA1C_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_HbA1C_header));
        }
        else if(view == findViewById(R.id.blood_test_read_here_triglyceride)) {
            openWebView.putExtra(BloodTestWebViewActivity.WEB_SITE_EXTRA,BloodTestWebViewActivity.TRIGLYCERIDE_ADDRESS);
            openWebView.putExtra(BloodTestWebViewActivity.ACTION_BAR_NAME_EXTRA,getString(R.string.blood_test_triglyceride_header));
        }
        startActivity(openWebView);
    }

}
