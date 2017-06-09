package com.hadassah.azrieli.lev_isha.core;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.ObservableWebView;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class HealthRecommendationsActivity extends AppCompatActivity {

    private PersonalProfile profile;
    public static final String EXTRA_SMOKE = "health_recommendations_extra_smoking";
    public static final String EXTRA_HISTORY = "health_recommendations_extra_history";
    public static final String EXTRA_HEIGHT = "health_recommendations_extra_height";
    public static final String EXTRA_WEIGHT = "health_recommendations_extra_weight";
    public static final String EXTRA_AGE = "health_recommendations_extra_age";
    private String smoke, history;
    private int height, weight, age;
    private ProgressBar progressBar;
    private ObservableWebView webView;
    private ImageView[] arrows = new ImageView[5];
    private ObjectAnimator[] arrowsAnimators = new ObjectAnimator[arrows.length];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_recommendations);
        try{getSupportActionBar().setTitle(R.string.personal_health_recommendation_label);} catch(Exception ignore){}
        Intent data = getIntent();
        profile = PersonalProfile.getInstance(this);
        smoke = data.getStringExtra(EXTRA_SMOKE);
        history = data.getStringExtra(EXTRA_HISTORY);
        height = data.getIntExtra(EXTRA_HEIGHT,-1);
        weight = data.getIntExtra(EXTRA_WEIGHT,-1);
        age = data.getIntExtra(EXTRA_AGE,-1);
        webView = (ObservableWebView)findViewById(R.id.personal_health_recommendation_webview);
        progressBar = (ProgressBar)findViewById(R.id.personal_health_recommendation_progress_bar);
        webView.setWebViewClient(new WebViewController());
        //webView.getSettings().setJavaScriptEnabled(true);
        arrows[0] = (ImageView)findViewById(R.id.personal_health_recommendation_scrolling_indication_icon1);
        arrows[1] = (ImageView)findViewById(R.id.personal_health_recommendation_scrolling_indication_icon2);
        arrows[2] = (ImageView)findViewById(R.id.personal_health_recommendation_scrolling_indication_icon3);
        arrows[3] = (ImageView)findViewById(R.id.personal_health_recommendation_scrolling_indication_icon4);
        arrows[4] = (ImageView)findViewById(R.id.personal_health_recommendation_scrolling_indication_icon5);
        webView.loadUrl("http://www.lev-isha.org/hra_result/?age="+age+"&smoke="+smoke+"&history="+history+"&bmi_weight="+weight+"&bmi_height="+height+"&approve=1");
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    public class WebViewController extends WebViewClient {

        boolean loadingFinished = true;
        boolean redirect = false;

        @TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String urlNewString = request.getUrl().toString();
            if (!loadingFinished)
                redirect = true;
            loadingFinished = false;
            view.loadUrl(urlNewString);
            return true;
        }


        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
            if (!loadingFinished)
                redirect = true;
            loadingFinished = false;
            view.loadUrl(urlNewString);
            return true;
        }

        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            loadingFinished = false;
        }

        public void onPageFinished(WebView view, String url) {
            if(!redirect)
                loadingFinished = true;
            if(loadingFinished && !redirect){
                progressBar.setVisibility(View.GONE);
            } else
                redirect = false;
            webView.scrollTo(50000,0);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(prefs.getBoolean("show_scrolling_indication_inside_health_recommendation_activity", true))
            {
                prefs.edit().putBoolean("show_scrolling_indication_inside_health_recommendation_activity",false).apply();
                presentScrollingIndication();
            }
        }
    }

    private void presentScrollingIndication() {
        final ImageView dimBackground = (ImageView)findViewById(R.id.personal_health_recommendation_dim_screen_background);
        dimBackground.setVisibility(View.VISIBLE);
        startAnimation();
        webView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t){
                dimBackground.setVisibility(View.GONE);
                stopAnimation();
                webView.setOnScrollChangedCallback(null);
            }
        });
    }

    private void startAnimation() {
        for(int i=0;i<arrows.length;i++)
        {
            arrowsAnimators[i] = ObjectAnimator.ofFloat(arrows[i], "alpha", 0f, 1f);
            arrowsAnimators[i].setInterpolator(new LinearInterpolator());
            arrowsAnimators[i].setRepeatCount(ObjectAnimator.INFINITE);
            arrowsAnimators[i].setRepeatMode(ObjectAnimator.REVERSE);
            arrowsAnimators[i].setDuration(1000);
            arrowsAnimators[i].setStartDelay(300*i);
            arrowsAnimators[i].start();
        }
    }

    private void stopAnimation() {
        for(int i=0;i<arrows.length;i++)
        {
            arrowsAnimators[i].end();
            arrows[i].setAlpha(0f);
        }
    }

}