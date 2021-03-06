package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if(!GeneralPurposeService.isServiceRunning())
            this.startService(new Intent(this,GeneralPurposeService.class));
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView logo = (ImageView)this.findViewById(R.id.splash_screen_logo);
        final Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.splash_screen_zoom_in);
        final Animation zoomOut = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.splash_screen_zoom_out);
        final Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
        zoomIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                zoomOut.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {}
                    public void onAnimationEnd(Animation animation) {
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
                    }
                    public void onAnimationRepeat(Animation animation) {}
                });
                logo.startAnimation(zoomOut);
            }
            public void onAnimationRepeat(Animation animation) {}
        });
        logo.startAnimation(zoomIn);
    }
}