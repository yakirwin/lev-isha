package com.hadassah.azrieli.lev_isha.utility;

import android.content.Context;
import android.content.res.Resources;

import com.hadassah.azrieli.lev_isha.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public class Checklist implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Checklist instance;
    private static final String FILE_NAME = "checklist_entries.data";
    private static final boolean DEBUG_DELETE_FILE = false; //KEEP THIS FALSE!

    public static Checklist getInstance(Context context) {
        if(instance != null)
            return instance;
        Resources res = context.getResources();
        File profileObject = new File(context.getFilesDir().getAbsolutePath()+"//"+FILE_NAME);
        if(DEBUG_DELETE_FILE)
            profileObject.delete();
        if(profileObject.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profileObject));
                instance = (Checklist)ois.readObject();
                ois.close();
                return instance;
            } catch(Exception exp){exp.printStackTrace();}
        }
        instance = new Checklist();
        return instance;
    }

    private Checklist() {

    }



}
