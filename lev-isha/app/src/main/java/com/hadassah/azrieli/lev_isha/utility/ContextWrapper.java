package com.hadassah.azrieli.lev_isha.utility;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class ContextWrapper extends android.content.ContextWrapper {

    public ContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, Locale newLocale) {
        Context toReturn = context;
        Resources res = toReturn.getResources();
        Configuration configuration = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            toReturn = toReturn.createConfigurationContext(configuration);
            Locale.setDefault(newLocale);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
            toReturn = toReturn.createConfigurationContext(configuration);
            Locale.setDefault(newLocale);
        }/* else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }*/
        return new ContextWrapper(toReturn);
    }
}

