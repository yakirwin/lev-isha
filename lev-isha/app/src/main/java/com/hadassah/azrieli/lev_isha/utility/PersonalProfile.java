package com.hadassah.azrieli.lev_isha.utility;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.core.PersonalProfileActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Avihu Harush on 05/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class PersonalProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final boolean DEBUG_DELETE_FILE = false; //KEEP THIS FALSE!

    private ArrayList<PersonalProfileEntry> entries;
    private static PersonalProfile instance;
    private static ArrayList<String> defaults;
    private static final int[] rKeys = {
            R.string.first_name,R.string.last_name,R.string.weight,
            R.string.height,R.string.bmi,R.string.latest_checkup};
    private static final int[] rKeysTypes = {
            PersonalProfileEntry.PLAIN_TEXT,PersonalProfileEntry.PLAIN_TEXT,PersonalProfileEntry.REAL_NUMBERS,
            PersonalProfileEntry.REAL_NUMBERS,PersonalProfileEntry.REAL_NUMBERS,PersonalProfileEntry.DATE};
    private static final String FILE_NAME = "personal_profile.data";

    public static PersonalProfile getInstance(Context context) {
        if(instance != null)
            return instance;
        Resources res = context.getResources();
        defaults = new ArrayList<>();
        for(int i=0;i<rKeys.length;i++)
            defaults.add(res.getText(rKeys[i]).toString());
        File profileObject = new File(context.getFilesDir().getAbsolutePath()+"//"+FILE_NAME);
        if(DEBUG_DELETE_FILE)
            profileObject.delete();
        if(profileObject.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profileObject));
                instance = (PersonalProfile)ois.readObject();
                ois.close();
                instance.updateDefaults();
                return instance;
            } catch(Exception exp){exp.printStackTrace();}
        }
        instance = new PersonalProfile();
        return instance;
    }

    private PersonalProfile() {
        entries = new ArrayList<>();
        updateDefaults();
    }

    private void updateDefaults() {
        for(int i = 0 ; i < defaults.size() ; i++)
            if(findEntryByName(defaults.get(i)) == null)
                entries.add(new PersonalProfileEntry(defaults.get(i),null, true,i,rKeysTypes[i]));
        rearrangeEntries();
    }

    public ArrayList<PersonalProfileEntry> getMissingValues() {
        ArrayList<PersonalProfileEntry> toReturn = new ArrayList<>();
        for(int i=0;i<entries.size();i++)
            if(entries.get(i).getValue() == null)
                toReturn.add(entries.get(i));
        return toReturn;
    }

    public String getValue(int index) {
        if(index >= entries.size())
            return null;
        PersonalProfileEntry entry = entries.get(index);
        if(entry == null)
            return null;
        return entry.getValue();
    }

    public boolean setValue(int index, String value) {
        if(index >= entries.size())
            return false;
        PersonalProfileEntry entry = entries.get(index);
        if(entry == null)
            return false;
        entry.setValue(value);
        return true;
    }

    public PersonalProfileEntry getEntry(int index) {
        if(index >= entries.size())
            return null;
        return entries.get(index);
    }

    public boolean commitChanges(Context context) {
        File profileObject = new File(context.getFilesDir().getAbsolutePath()+"//"+FILE_NAME);
        profileObject.delete();
        try{profileObject.createNewFile();}catch(Exception ignore){}
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(profileObject));
            oos.writeObject(instance);
            oos.close();
            return true;
        } catch(Exception exp){exp.printStackTrace();}
        return false;
    }

    public PersonalProfileEntry addNewField(String name, String value, int type) {
        if(findEntryByName(name) != null)
            return null;
        PersonalProfileEntry newEntry = new PersonalProfileEntry(name,value, false,entries.size(),type);
        entries.add(newEntry);
        return newEntry;
    }

    public int getEntriesAmount() {
        return entries.size();
    }

    public Iterator getIterator() {
        return entries.iterator();
    }

    public PersonalProfileEntry findEntryByName(String name) {
        for(int i = 0 ; i < entries.size() ; i++)
            if(entries.get(i).getName().equals(name))
                return entries.get(i);
        return null;
    }

    public PersonalProfileEntry findEntryByValue(String val) {
        for(int i = 0 ; i < entries.size() ; i++)
            if(entries.get(i).getValue().equals(val))
                return entries.get(i);
        return null;
    }

    public ArrayList<PersonalProfileEntry> getEssentials() {
        ArrayList<PersonalProfileEntry> toReturn = new ArrayList<>();
        for(int i = 0 ; i < entries.size() ; i++)
            if(entries.get(i).isEssential())
                toReturn.add(entries.get(i));
        return toReturn;
    }

    public PersonalProfileEntry deleteFieldByIndex(int index) {
        PersonalProfileEntry entry = entries.get(index);
        if(entry == null || entry.isEssential())
            return null;
        entries.remove(index);
        rearrangeEntries();
        return entry;
    }

    public PersonalProfileEntry deleteFieldByEntry(PersonalProfileEntry entry) {
        if(entry == null || entry.isEssential())
            return null;
        PersonalProfileEntry toDelete;
        for(int i=0;i<entries.size();i++)
            if(entries.get(i).getName().equals(entry.getName()))
            {
                toDelete = entries.get(i);
                entries.remove(i);
                rearrangeEntries();
                return toDelete;
            }
        return null;
    }

    private void rearrangeEntries() {
        Collections.sort(entries);
        for(int i = 0 ; i < entries.size() ; i++)
            entries.get(i).setIndex(i);
    }

    public ArrayList<PersonalProfileEntry> getEntriesCopy() {
        return new ArrayList<>(entries);
    }

}
