package com.hadassah.azrieli.lev_isha.utility;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.core.MainMenuActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Avihu Harush on 01/06/2017
 * E-Mail: tchvu3@gmail.com
 */
public abstract class OverallNotificationManager {

    public static final String NOTIFICATION_BIRTHDAY_EXTRA_ID = "notification_birthday_id";
    public static final String NOTIFICATION_HALF_YEAR_EXTRA_ID = "notification_half_year_id";
    public static final String NOTIFICATION_DAY_BEFORE_DOCTOR_EXTRA_ID = "notification_day_before_doctor_id";
    public static final String NOTIFICATION_10_MIN_BEFORE_DOCTOR_EXTRA_ID = "notification_10_min_before_doctor_id";
    public static final String NOTIFICATION_BIRTHDAY_OBJECT = "notification_birthday_object";
    public static final String NOTIFICATION_HALF_YEAR_OBJECT = "notification_half_year_object";
    public static final String NOTIFICATION_DAY_BEFORE_DOCTOR_OBJECT = "notification_day_before_doctor_object";
    public static final String NOTIFICATION_10_MIN_BEFORE_DOCTOR_OBJECT = "notification_10_min_before_doctor_object";
    public static final int NOTIFICATION_BIRTHDAY_ID = 18000;
    public static final int NOTIFICATION_HALF_YEAR_ID = 18001;
    public static final int NOTIFICATION_DAY_BEFORE_DOCTOR_ID = 18002;
    public static final int NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID = 18003;
    public static final int NO_ADDITIONAL_ID = -10;

    public static void setUpNotificationTimers(Context context, int code) {
        switch(code) {
            case NO_ADDITIONAL_ID : break;
            case NOTIFICATION_BIRTHDAY_ID : setupBirthdayNotification(context); return;
            case NOTIFICATION_HALF_YEAR_ID : setupHalfYearNotification(context); return;
            case NOTIFICATION_DAY_BEFORE_DOCTOR_ID : setupDayBeforeDoctorNotification(context); return;
            case NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID : setupTenMinsBeforeDoctorNotification(context); return;
        }
        setupBirthdayNotification(context);
        setupHalfYearNotification(context);
        setupDayBeforeDoctorNotification(context);
        setupTenMinsBeforeDoctorNotification(context);
    }

    private static void setupBirthdayNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String nextTimeToShowInMemory = prefs.getString("next_time_to_show_birthday_notification",null);
        PersonalProfile personalProfile = PersonalProfile.getInstance(context);
        PersonalProfileEntry birthDayEntry = personalProfile.findEntryByName(context.getString(R.string.birth_date));
        DateFormat df = DateFormat.getDateInstance();
        String birthDayString = birthDayEntry.getValue();
        Calendar birthDay = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        try{birthDay.setTime(df.parse(birthDayString));}catch(Exception ignore){return;}
        Calendar nextBirthDay = Calendar.getInstance();
        nextBirthDay.set(today.get(Calendar.YEAR),birthDay.get(Calendar.MONTH),birthDay.get(Calendar.DAY_OF_MONTH));
        if(nextBirthDay.getTimeInMillis() < today.getTimeInMillis())
            nextBirthDay.add(Calendar.YEAR,1);
        if(nextTimeToShowInMemory == null)
            prefs.edit().putString("next_time_to_show_birthday_notification",df.format(nextBirthDay.getTime())).apply();
        else {
            Calendar calendarObjInMemory = Calendar.getInstance();
            try{calendarObjInMemory.setTime(df.parse(nextTimeToShowInMemory));}catch(Exception ignore){return;}
            nextBirthDay = (Calendar)calendarObjInMemory.clone();
            nextBirthDay.add(Calendar.YEAR,1);
            if(calendarObjInMemory.getTimeInMillis() > today.getTimeInMillis())
                return;
            prefs.edit().putString("next_time_to_show_birthday_notification",df.format(nextBirthDay.getTime())).apply();
        }
        Intent intent = new Intent(context, MainMenuActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, NOTIFICATION_BIRTHDAY_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_message_birthday_header))
                .setContentText(getBirthdayContentText(context))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(activity);
        Notification notification = builder.build();
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_BIRTHDAY_EXTRA_ID, NOTIFICATION_BIRTHDAY_ID);
        notificationIntent.putExtra(NOTIFICATION_BIRTHDAY_OBJECT, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_BIRTHDAY_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextBirthDay.getTimeInMillis(), pendingIntent);
    }

    private static String getBirthdayContentText(Context context) {
        String toReturn = "";
        PersonalProfile personalProfile = PersonalProfile.getInstance(context);
        PersonalProfileEntry nameEntry = personalProfile.findEntryByName(context.getString(R.string.name));
        String name = nameEntry.getValue();
        toReturn += context.getString(R.string.notification_message_happy_birthday);
        toReturn += (name != null) ? name+", " : ", ";
        toReturn += context.getString(R.string.notification_message_birthday_body);
        return toReturn;
    }

    private static void setupHalfYearNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
        Calendar today = Calendar.getInstance();
        Calendar nextTimeToShow = Calendar.getInstance();
        nextTimeToShow.add(Calendar.MONTH,6);
        String nextTimeToShowInMemory = prefs.getString("next_time_to_show_half_year_notification",null);
        if(nextTimeToShowInMemory == null)
            prefs.edit().putString("next_time_to_show_half_year_notification",df.format(nextTimeToShow.getTime())).apply();
        else {
            Calendar calendarObjInMemory = Calendar.getInstance();
            try{calendarObjInMemory.setTime(df.parse(nextTimeToShowInMemory));}catch(Exception ignore){return;}
            nextTimeToShow = (Calendar)calendarObjInMemory.clone();
            nextTimeToShow.add(Calendar.MONTH,6);
            if(calendarObjInMemory.getTimeInMillis() > today.getTimeInMillis())
                return;
            prefs.edit().putString("next_time_to_show_half_year_notification",df.format(nextTimeToShow.getTime())).apply();
        }
        Intent intent = new Intent(context, MainMenuActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, NOTIFICATION_HALF_YEAR_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_message_half_year_header))
                .setContentText(context.getString(R.string.notification_message_half_year_body))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(activity);
        Notification notification = builder.build();
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_HALF_YEAR_EXTRA_ID, NOTIFICATION_HALF_YEAR_ID);
        notificationIntent.putExtra(NOTIFICATION_HALF_YEAR_OBJECT, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_HALF_YEAR_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTimeToShow.getTimeInMillis(), pendingIntent);
    }

    private static void setupDayBeforeDoctorNotification(Context context) {

    }

    private static void setupTenMinsBeforeDoctorNotification(Context context) {

    }

}


