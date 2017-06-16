package com.hadassah.azrieli.lev_isha.core;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.ObservableWebView;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;

public class BloodTestWebViewActivity extends AppCompatActivity {

    public static final String ACTION_BAR_NAME_EXTRA = "blood_test_extra_string_for_action_bar_title";
    public static final String WEB_SITE_EXTRA = "blood_test_extra_string_for_website_address";
    public static final String BMI_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%94%D7%A9%D7%9E%D7%A0%D7%94-%D7%9C%D7%90-%D7%92%D7%96%D7%99%D7%A8%D7%94-%D7%9E%D7%A9%D7%9E%D7%99%D7%99%D7%9D/";
    public static final String BLOOD_PRESSURE_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%9C%D7%97%D7%A5-%D7%93%D7%9D/";
    public static final String CHOLESTEROL_GENERAL_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%9B%D7%95%D7%9C%D7%A1%D7%98%D7%A8%D7%95%D7%9C-%D7%91%D7%91%D7%93%D7%99%D7%A7%D7%AA-%D7%93%D7%9D/";
    public static final String CHOLESTEROL_LDL_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%9B%D7%95%D7%9C%D7%A1%D7%98%D7%A8%D7%95%D7%9C-%D7%91%D7%91%D7%93%D7%99%D7%A7%D7%AA-%D7%93%D7%9D/";
    public static final String CHOLESTEROL_HDL_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%9B%D7%95%D7%9C%D7%A1%D7%98%D7%A8%D7%95%D7%9C-%D7%91%D7%91%D7%93%D7%99%D7%A7%D7%AA-%D7%93%D7%9D/";
    public static final String TRIGLYCERIDE_ADDRESS = "http://www.lev-isha.org/%D7%98%D7%A8%D7%99%D7%92%D7%9C%D7%99%D7%A6%D7%A8%D7%99%D7%93%D7%99%D7%9D/";
    public static final String GLUCOSE_FASTING_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%A8%D7%9E%D7%AA-%D7%94%D7%A1%D7%95%D7%9B%D7%A8-%D7%91%D7%93%D7%9D/";
    public static final String HBA1C_ADDRESS = "http://www.lev-isha.org/portfolio/%D7%A8%D7%9E%D7%AA-%D7%94%D7%A1%D7%95%D7%9B%D7%A8-%D7%91%D7%93%D7%9D/";


    private ImageView[] arrows = new ImageView[5];
    private ObjectAnimator[] arrowsAnimators = new ObjectAnimator[arrows.length];
    private ProgressBar progressBar;
    private ObservableWebView webView;
    private String siteAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_test_web_view);
        siteAddress = getIntent().getStringExtra(WEB_SITE_EXTRA);
        if(!checkSitesIntegrity())
            finish();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getIntent().getStringExtra(ACTION_BAR_NAME_EXTRA));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        webView = (ObservableWebView)findViewById(R.id.blood_test_webview);
        progressBar = (ProgressBar)findViewById(R.id.blood_test_progress_bar);
        webView.setWebViewClient(new WebViewController());
        //webView.getSettings().setJavaScriptEnabled(true);
        arrows[0] = (ImageView)findViewById(R.id.blood_test_scrolling_indication_icon1);
        arrows[1] = (ImageView)findViewById(R.id.blood_test_scrolling_indication_icon2);
        arrows[2] = (ImageView)findViewById(R.id.blood_test_scrolling_indication_icon3);
        arrows[3] = (ImageView)findViewById(R.id.blood_test_scrolling_indication_icon4);
        arrows[4] = (ImageView)findViewById(R.id.blood_test_scrolling_indication_icon5);
        webView.loadUrl(siteAddress);
        if(!GeneralPurposeService.isServiceRunning())
            this.startService(new Intent(this,GeneralPurposeService.class));
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private class WebViewController extends WebViewClient {

        private boolean loadingFinished = true;
        private boolean redirect = false;

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
            if(prefs.getBoolean("show_scrolling_indication_inside_blood_test_web_activity", true))
            {
                prefs.edit().putBoolean("show_scrolling_indication_inside_blood_test_web_activity",false).apply();
                presentScrollingIndication();
            }
        }
    }

    private void presentScrollingIndication() {
        final ImageView dimBackground = (ImageView)findViewById(R.id.blood_test_dim_screen_background);
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

    private boolean checkSitesIntegrity() {
        return siteAddress.equals(BMI_ADDRESS) || siteAddress.equals(BLOOD_PRESSURE_ADDRESS) ||
                siteAddress.equals(CHOLESTEROL_GENERAL_ADDRESS) || siteAddress.equals(CHOLESTEROL_LDL_ADDRESS) ||
                siteAddress.equals(CHOLESTEROL_HDL_ADDRESS) || siteAddress.equals(TRIGLYCERIDE_ADDRESS) ||
                siteAddress.equals(GLUCOSE_FASTING_ADDRESS) || siteAddress.equals(HBA1C_ADDRESS);
    }

}
