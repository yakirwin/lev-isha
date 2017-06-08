package com.hadassah.azrieli.lev_isha.utility;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GeneralPurposeService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId) {
        OverallNotificationManager.setUpNotificationTimers(this, OverallNotificationManager.NO_ADDITIONAL_ID);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent){return null;}
}
