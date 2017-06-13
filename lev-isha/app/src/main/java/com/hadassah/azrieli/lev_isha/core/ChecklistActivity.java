package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
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

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

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
        if(item.getItemId() == R.id.checklist_options_clear_after) {
            customPagerAdapter.clearAfter();
            return true;
        }
        if(item.getItemId() == R.id.checklist_options_clear_before) {
            customPagerAdapter.clearBefore();
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
                case 0: return beforeFragment;
                case 1: return afterFragment;
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
                case 0: return getString(R.string.check_list_1st_part_header);
                case 1: return getString(R.string.check_list_2nd_part_header);
                default: return null;
            }
        }

        void clearBefore() {
            beforeFragment.clearForm();
        }

        void clearAfter() {
            afterFragment.clearForm();
        }
    }

}
