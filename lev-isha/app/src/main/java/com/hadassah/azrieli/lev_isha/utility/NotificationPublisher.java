package com.hadassah.azrieli.lev_isha.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_10_MIN_BEFORE_DOCTOR_EXTRA_ID;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_10_MIN_BEFORE_DOCTOR_OBJECT;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_BIRTHDAY_EXTRA_ID;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_BIRTHDAY_OBJECT;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_DAY_BEFORE_DOCTOR_EXTRA_ID;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_DAY_BEFORE_DOCTOR_OBJECT;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_HALF_YEAR_EXTRA_ID;
import static com.hadassah.azrieli.lev_isha.utility.OverallNotificationManager.NOTIFICATION_HALF_YEAR_OBJECT;

public class NotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if(!GeneralPurposeService.isServiceRunning())
                context.startService(new Intent(context,GeneralPurposeService.class));
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION_BIRTHDAY_OBJECT);
        if(notification == null)
            notification = intent.getParcelableExtra(NOTIFICATION_HALF_YEAR_OBJECT);
        if(notification == null)
            notification = intent.getParcelableExtra(NOTIFICATION_DAY_BEFORE_DOCTOR_OBJECT);
        if(notification == null)
            notification = intent.getParcelableExtra(NOTIFICATION_10_MIN_BEFORE_DOCTOR_OBJECT);
        int code = intent.getIntExtra(NOTIFICATION_BIRTHDAY_EXTRA_ID, -1);
        if(code == -1)
            code = intent.getIntExtra(NOTIFICATION_HALF_YEAR_EXTRA_ID, -1);
        if(code == -1)
            code = intent.getIntExtra(NOTIFICATION_DAY_BEFORE_DOCTOR_EXTRA_ID, -1);
        if(code == -1)
            code = intent.getIntExtra(NOTIFICATION_10_MIN_BEFORE_DOCTOR_EXTRA_ID, -1);
        notificationManager.notify(code, notification);
        OverallNotificationManager.setUpNotificationTimers(context,code);
    }

}
