package com.hadassah.azrieli.lev_isha.utility;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.core.ChecklistActivity;
import com.hadassah.azrieli.lev_isha.core.MainMenuActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.support.v4.app.NotificationManagerCompat.IMPORTANCE_MAX;

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
    private static final int NOTIFICATION_HALF_YEAR_ID = 18001;
    public static final int NOTIFICATION_DAY_BEFORE_DOCTOR_ID = 18002;
    public static final int NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID = 18003;
    public static final int NO_ADDITIONAL_ID = -10;

    public static void setUpNotificationTimers(Context context, int code) {
        context = ContextWrapper.wrap(context, PersonalProfile.getCurrentLocale());
        switch(code) {
            case NOTIFICATION_BIRTHDAY_ID : setupBirthdayNotification(context); return;
            case NOTIFICATION_HALF_YEAR_ID : setupHalfYearNotification(context); return;
            case NOTIFICATION_DAY_BEFORE_DOCTOR_ID : setupDayBeforeDoctorNotification(context); return;
            case NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID : setupTenMinsBeforeDoctorNotification(context); return;
            default : break;
        }
        setupBirthdayNotification(context);
        setupHalfYearNotification(context);
        setupDayBeforeDoctorNotification(context);
        setupTenMinsBeforeDoctorNotification(context);
    }

    @SuppressLint("ApplySharedPref")
    private static void setupBirthdayNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long nextTimeToShowInMemory = prefs.getLong("next_time_to_show_birthday_notification",-1);
        PersonalProfileEntry birthDayEntry = PersonalProfile.getInstance(context).findEntryByName(context.getString(R.string.birth_date));
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, PersonalProfile.getCurrentLocale());
        String birthDayString = birthDayEntry.getValue();
        Calendar birthDay = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH),0,0,0);
        today.set(Calendar.MILLISECOND,0);
        try{birthDay.setTime(df.parse(birthDayString));}catch(Exception ignore){return;}
        birthDay.set(birthDay.get(Calendar.YEAR),birthDay.get(Calendar.MONTH),birthDay.get(Calendar.DAY_OF_MONTH),0,0,0);
        birthDay.set(Calendar.MILLISECOND,0);
        Calendar nextBirthDay = Calendar.getInstance();
        nextBirthDay.set(today.get(Calendar.YEAR),birthDay.get(Calendar.MONTH),birthDay.get(Calendar.DAY_OF_MONTH),0,0,0);
        nextBirthDay.set(Calendar.MILLISECOND,0);
        if(nextBirthDay.getTimeInMillis() < today.getTimeInMillis())
            nextBirthDay.add(Calendar.YEAR,1);
        nextBirthDay.set(Calendar.HOUR_OF_DAY,10);
        nextBirthDay.set(Calendar.MINUTE,0);
        if(nextTimeToShowInMemory == -1)
            prefs.edit().putLong("next_time_to_show_birthday_notification",nextBirthDay.getTimeInMillis()).apply();
        else {
            Calendar calendarObjInMemory = Calendar.getInstance();
            calendarObjInMemory.setTimeInMillis(nextTimeToShowInMemory);
            if(calendarObjInMemory.get(Calendar.DAY_OF_MONTH) != nextBirthDay.get(Calendar.DAY_OF_MONTH) ||
                    calendarObjInMemory.get(Calendar.MONTH) != nextBirthDay.get(Calendar.MONTH))
            {
                prefs.edit().putLong("next_time_to_show_birthday_notification",-1).commit();
                setupBirthdayNotification(context);
                return;
            }
            nextBirthDay = (Calendar)calendarObjInMemory.clone();
            nextBirthDay.add(Calendar.YEAR,1);
            if(calendarObjInMemory.getTimeInMillis() > today.getTimeInMillis())
                return;
            prefs.edit().putLong("next_time_to_show_birthday_notification",nextBirthDay.getTimeInMillis()).apply();
        }
        Notification notification = createBirthdayNotification(context);
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_BIRTHDAY_EXTRA_ID, NOTIFICATION_BIRTHDAY_ID);
        notificationIntent.putExtra(NOTIFICATION_BIRTHDAY_OBJECT, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_BIRTHDAY_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextBirthDay.getTimeInMillis(), pendingIntent);
    }

    private static void setupHalfYearNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH),10,0,0);
        today.set(Calendar.MILLISECOND,0);
        Calendar nextTimeToShow = Calendar.getInstance();
        nextTimeToShow.set(nextTimeToShow.get(Calendar.YEAR),nextTimeToShow.get(Calendar.MONTH),nextTimeToShow.get(Calendar.DAY_OF_MONTH),10,0,0);
        nextTimeToShow.set(Calendar.MILLISECOND,0);
        nextTimeToShow.add(Calendar.MONTH,6);
        long nextTimeToShowInMemory = prefs.getLong("next_time_to_show_half_year_notification",-1);
        if(nextTimeToShowInMemory == -1)
            prefs.edit().putLong("next_time_to_show_half_year_notification",nextTimeToShow.getTimeInMillis()).apply();
        else {
            Calendar calendarObjInMemory = Calendar.getInstance();
            calendarObjInMemory.setTimeInMillis(nextTimeToShowInMemory);
            nextTimeToShow = (Calendar)calendarObjInMemory.clone();
            nextTimeToShow.add(Calendar.MONTH,6);
            if(calendarObjInMemory.getTimeInMillis() > today.getTimeInMillis())
                return;
            prefs.edit().putLong("next_time_to_show_half_year_notification",nextTimeToShow.getTimeInMillis()).apply();
        }
        Notification notification = createHalfYearNotification(context);
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_HALF_YEAR_EXTRA_ID, NOTIFICATION_HALF_YEAR_ID);
        notificationIntent.putExtra(NOTIFICATION_HALF_YEAR_OBJECT, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_HALF_YEAR_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTimeToShow.getTimeInMillis(), pendingIntent);
    }

    @SuppressLint("ApplySharedPref")
    private static void setupDayBeforeDoctorNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long appointmentDateInMs = prefs.getLong("next_doctor_appointment_date_in_ms",-1);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        Calendar appointment = Calendar.getInstance();
        appointment.setTimeInMillis(appointmentDateInMs);
        appointment.set(Calendar.SECOND,0);
        appointment.set(Calendar.MILLISECOND,0);
        if(today.getTimeInMillis() > appointment.getTimeInMillis() ||
                (today.getTimeInMillis() < appointment.getTimeInMillis() && appointment.getTimeInMillis()-today.getTimeInMillis() < 86400000))
            return;
        if(prefs.getBoolean("next_doctor_appointment_showed_one_day_before",false))
            return;
        prefs.edit().putBoolean("next_doctor_appointment_showed_one_day_before",true).commit();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(appointment.getTimeInMillis());
        alarmTime.set(Calendar.HOUR_OF_DAY,10);
        alarmTime.set(Calendar.MINUTE,0);
        alarmTime.add(Calendar.DAY_OF_MONTH,-1);
        Notification notification = createDayBeforeDoctorNotification(context, appointment);
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_DAY_BEFORE_DOCTOR_EXTRA_ID, NOTIFICATION_DAY_BEFORE_DOCTOR_ID);
        notificationIntent.putExtra(NOTIFICATION_DAY_BEFORE_DOCTOR_OBJECT, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_DAY_BEFORE_DOCTOR_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
    }

    @SuppressLint("ApplySharedPref")
    private static void setupTenMinsBeforeDoctorNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long appointmentDateInMs = prefs.getLong("next_doctor_appointment_date_in_ms",-1);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        Calendar appointment = Calendar.getInstance();
        appointment.setTimeInMillis(appointmentDateInMs);
        appointment.set(Calendar.SECOND,0);
        appointment.set(Calendar.MILLISECOND,0);
        if(today.getTimeInMillis() > appointment.getTimeInMillis() ||
                ((today.getTimeInMillis() < appointment.getTimeInMillis() && appointment.getTimeInMillis()-today.getTimeInMillis() < 600000)))
            return;
        if(prefs.getBoolean("next_doctor_appointment_showed_ten_min_before",false))
            return;
        prefs.edit().putBoolean("next_doctor_appointment_showed_ten_min_before",true).commit();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(appointment.getTimeInMillis());
        alarmTime.add(Calendar.MINUTE, -10);
        Notification notification = createTenMinsBeforeNotification(context);
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_10_MIN_BEFORE_DOCTOR_EXTRA_ID, NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID);
        notificationIntent.putExtra(NOTIFICATION_10_MIN_BEFORE_DOCTOR_OBJECT, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
    }

    public static void cancelDoctorAppointment(Context context) {

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_DAY_BEFORE_DOCTOR_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("next_doctor_appointment_showed_ten_min_before",false).apply();
        prefs.edit().putBoolean("next_doctor_appointment_showed_one_day_before",false).apply();
    }

    private static String getBirthdayContentText(Context context) {
        String toReturn = "";
        PersonalProfile personalProfile = PersonalProfile.getInstance(context);
        PersonalProfileEntry nameEntry = personalProfile.findEntryByName(context.getString(R.string.name));
        String name = nameEntry.getValue();
        toReturn += context.getString(R.string.notification_message_happy_birthday);
        toReturn += (name != null) ? " "+name+", " : ", ";
        toReturn += context.getString(R.string.notification_message_birthday_body);
        return toReturn;
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDeyBeforeDoctorContentText(Context context, Calendar appointment) {
        String docName = PreferenceManager.getDefaultSharedPreferences(context).getString("edit_text_doctor_name",null);
        String toReturn = "";
        toReturn += (context.getString(R.string.notification_message_day_before_doctor_header) + " ");
        toReturn += (context.getString(R.string.notification_message_day_before_doctor_body_1) + " " + new SimpleDateFormat("HH:mm").format(appointment.getTime())+", ");
        if(docName != null && docName.length() != 0)
            toReturn += (context.getString(R.string.notification_message_day_before_doctor_body_2) + " " + docName + ", ");
        toReturn += context.getString(R.string.notification_message_day_before_doctor_body_3);
        return toReturn;
    }

    private static Notification createDayBeforeDoctorNotification(Context context, Calendar appointment) {
        Intent intent = new Intent(context, ChecklistActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, NOTIFICATION_DAY_BEFORE_DOCTOR_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(context.getString(R.string.notification_message_day_before_doctor_header))
                .setContentText(getDeyBeforeDoctorContentText(context,appointment))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getDeyBeforeDoctorContentText(context,appointment)))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setShowWhen(false)
                .setPriority(IMPORTANCE_MAX)
                .setContentIntent(activity);
        return builder.build();
    }

    private static Notification createHalfYearNotification(Context context) {
        Intent intent = new Intent(context, MainMenuActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, NOTIFICATION_HALF_YEAR_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(context.getString(R.string.notification_message_half_year_header))
                .setContentText(context.getString(R.string.notification_message_half_year_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getBirthdayContentText(context)))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setShowWhen(false)
                .setPriority(IMPORTANCE_MAX)
                .setContentIntent(activity);
        return builder.build();
    }

    private static Notification createTenMinsBeforeNotification(Context context) {
        Intent intent = new Intent(context, ChecklistActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, NOTIFICATION_10_MIN_BEFORE_DOCTOR_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentTitle(context.getString(R.string.notification_message_10_min_before_doctor_header))
                .setContentText(context.getString(R.string.notification_message_10_min_before_doctor_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_message_10_min_before_doctor_body)))
                .setAutoCancel(true)
                .setShowWhen(false)
                .addAction(0,context.getString(R.string.open_up_the_app),activity)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(IMPORTANCE_MAX)
                .setContentIntent(activity);
        return builder.build();
    }

    private static Notification createBirthdayNotification(Context context) {
        Intent intent = new Intent(context, MainMenuActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, NOTIFICATION_BIRTHDAY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(context.getString(R.string.notification_message_birthday_header))
                .setContentText(getBirthdayContentText(context))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getBirthdayContentText(context)))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setShowWhen(false)
                .setPriority(IMPORTANCE_MAX)
                .setContentIntent(activity);
        return builder.build();
    }
}


