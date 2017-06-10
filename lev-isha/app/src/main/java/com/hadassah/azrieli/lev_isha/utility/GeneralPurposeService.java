package com.hadassah.azrieli.lev_isha.utility;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Locale;

public class GeneralPurposeService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId) {
        OverallNotificationManager.setUpNotificationTimers(this, OverallNotificationManager.NO_ADDITIONAL_ID);
        return super.onStartCommand(intent, flags, startId);
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent){return null;}

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (GeneralPurposeService.class.getName().equals(service.service.getClassName()))
                return true;
        return false;
    }

}
