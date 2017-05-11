package com.hadassah.azrieli.lev_isha.core;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_recommendations);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent data = getIntent();
        profile = PersonalProfile.getInstance(this);
        smoke = data.getStringExtra(EXTRA_SMOKE);
        history = data.getStringExtra(EXTRA_HISTORY);
        height = data.getIntExtra(EXTRA_HEIGHT,-1);
        weight = data.getIntExtra(EXTRA_WEIGHT,-1);
        age = data.getIntExtra(EXTRA_AGE,-1);
        WebView webView;
        webView = (WebView)findViewById(R.id.personal_health_recommendation_webview);
        progressBar = (ProgressBar)findViewById(R.id.personal_health_recommendation_progress_bar);
        webView.setWebViewClient(new WebViewController());
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.lev-isha.org/hra_result/?age="+age+"&smoke="+smoke+"&history="+history+"&bmi_weight="+weight+"&bmi_height="+height+"&approve=1");

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
        }

    }


}