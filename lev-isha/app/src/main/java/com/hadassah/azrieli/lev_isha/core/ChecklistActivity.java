package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

public class ChecklistActivity extends AppCompatActivity {

    private static customPager customPagerAdapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        customPagerAdapter = new customPager(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(customPagerAdapter);
        mViewPager.setCurrentItem(1);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(R.string.title_activity_check_list);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(!GeneralPurposeService.isServiceRunning())
            this.startService(new Intent(this,GeneralPurposeService.class));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(prefs.getBoolean("first_time_entering_checklist_activity",true)) {
            prefs.edit().putBoolean("first_time_entering_checklist_activity", false).apply();
            showFirstTimeMessage();
        }
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checklist_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.checklist_options_clear_both) {
            customPagerAdapter.clearBefore();
            customPagerAdapter.clearAfter();
            return true;
        }
        else if(item.getItemId() == R.id.checklist_options_clear_after) {
            customPagerAdapter.clearAfter();
            return true;
        }
        else if(item.getItemId() == R.id.checklist_options_clear_before) {
            customPagerAdapter.clearBefore();
            return true;
        }
        else if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private class customPager extends FragmentPagerAdapter {

        private BeforeDoctorFragment beforeFragment = (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() >= 1) ? (BeforeDoctorFragment)getSupportFragmentManager().getFragments().get(0) : BeforeDoctorFragment.newInstance();
        private AfterDoctorFragment afterFragment = (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() >= 2) ? (AfterDoctorFragment)getSupportFragmentManager().getFragments().get(1) : AfterDoctorFragment.newInstance();

        customPager(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0: return afterFragment;
                case 1: return beforeFragment;
                default: return null;
            }
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if(object == null)
                return;
            if(object instanceof BeforeDoctorFragment && beforeFragment.isVisible())
                beforeFragment.reloadSavedFields();
            else if(object instanceof AfterDoctorFragment && afterFragment.isVisible())
                afterFragment.reloadSavedFields();
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.check_list_at_the_doctor_header);
                case 1: return getString(R.string.check_list_prep_to_the_doctor_header);
                default: return null;
            }
        }

        private void clearBefore() {
            beforeFragment.clearForm();
        }

        private void clearAfter() {
            afterFragment.clearForm();
        }
    }


    public void showFirstTimeMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.title_activity_check_list));
        builder.setMessage(getResources().getText(R.string.checklist_first_time_message));
        builder.setPositiveButton(R.string.understood,null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
