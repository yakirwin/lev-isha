package com.hadassah.azrieli.lev_isha.utility;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Locale;

public class GeneralPurposeService extends Service {

    private static boolean isRunning = false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        OverallNotificationManager.setUpNotificationTimers(this, OverallNotificationManager.NO_ADDITIONAL_ID);
        return super.onStartCommand(intent, flags, startId);
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    public IBinder onBind(Intent intent){return null;}

    public static boolean isServiceRunning() {
        return isRunning;
    }

}
