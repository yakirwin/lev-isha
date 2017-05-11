package com.hadassah.azrieli.lev_isha.core;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hadassah.azrieli.lev_isha.R;

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class ChecklistActivity extends AppCompatActivity {

    private static customPager customPagerAdapter = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        /*ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (customPagerAdapter != null)
            mViewPager.setAdapter(customPagerAdapter);
        else
            mViewPager.setAdapter(customPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);*/
    }


    class customPager extends FragmentPagerAdapter {

        BeforeDoctorFragment beforeFragment = (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() >= 1) ? (BeforeDoctorFragment)getSupportFragmentManager().getFragments().get(0) : BeforeDoctorFragment.newInstance();
        AfterDoctorFragment afterFragment = (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() >= 2) ? (AfterDoctorFragment)getSupportFragmentManager().getFragments().get(1) : AfterDoctorFragment.newInstance();

        public customPager(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0: return beforeFragment;
                case 1: return afterFragment;
                default: return null;
            }
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
    }



}
