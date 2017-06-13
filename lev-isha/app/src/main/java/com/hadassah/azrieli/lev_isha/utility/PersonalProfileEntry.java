package com.hadassah.azrieli.lev_isha.utility;

import android.support.annotation.NonNull;
import android.text.InputType;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Avihu Harush on 05/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class PersonalProfileEntry implements Serializable, Comparable<PersonalProfileEntry> {

    private static final long serialVersionUID = 1L;

    public static final int PLAIN_TEXT = InputType.TYPE_CLASS_TEXT;
    public static final int REAL_NUMBERS = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
    public static final int DATE = InputType.TYPE_CLASS_DATETIME;
    public static final int FINITE_STATES = InputType.TYPE_NULL;
    public static final String YES_VALUE = "finite_options_yes";
    public static final String NO_VALUE = "finite_options_no";
    public static final String MAYBE_VALUE = "finite_options_dont";

    private String name;
    private String val;
    private Calendar modified;
    private boolean essential;
    private int index;
    private int inputType;

    public PersonalProfileEntry(String name, String val, boolean essential,int index, int inputType) {
        this.name = name;
        this.val = val;
        this.modified = Calendar.getInstance();
        this.essential = essential;
        this.index = index;
        this.inputType = inputType;
    }

    public String getName() {
        return name;
    }

    public Calendar getLastModified() {
        return modified;
    }

    public String getValue() {
        return val;
    }

    public void setValue(String val) {
        if(inputType == FINITE_STATES)
            if(!val.equals(YES_VALUE) && !val.equals(NO_VALUE) && !val.equals(MAYBE_VALUE))
                return;
        this.val = val;
        this.modified = Calendar.getInstance();
    }

    public boolean isEssential() {
        return essential;
    }

    public boolean setIndex(int newIndex) {
        if(newIndex < 0)
            return false;
        index = newIndex;
        return true;
    }

    public int getIndex() {
        return index;
    }

    public int getInputType() {
        return inputType;
    }

    public boolean setInputType(int newType) {
        if( newType != PLAIN_TEXT && newType != DATE && newType != REAL_NUMBERS)
            return false;
        inputType = newType;
        return true;
    }

    public int compareTo(@NonNull PersonalProfileEntry personalProfileEntry) {
        return (this.index > personalProfileEntry.index) ? 1 : -1;
    }
}
