package com.hadassah.azrieli.lev_isha.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.hadassah.azrieli.lev_isha.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Avihu Harush on 25/05/2017
 * E-Mail: tchvu3@gmail.com
 */

public abstract class VoiceRecorder {

    private static MediaRecorder recorder = null;
    private static final String VOICE_RECORDS_FOLDER = "Lev_Isha_Voice_Records";
    private static final boolean forceIsraelFileNameFormat = true;
    private static File lastFileRecorded = null;

    public static boolean recordToNewFile(Context context) {
        if(recorder != null)
            return false;
        recorder = new MediaRecorder();
        try{recorder.setAudioSource(MediaRecorder.AudioSource.MIC);}
        catch(Exception failed) {
            Toast.makeText(context, context.getText(R.string.failed_to_start_record), Toast.LENGTH_SHORT).show();
            recorder = null;
            return false;
        }
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        String fileLocation = getFolderLocation() + getNameFormat() + ".mp4";
        File outputFile = new File(fileLocation);
        boolean folderExist, fileExist;
        if(!(folderExist = outputFile.getParentFile().exists()))
            folderExist = outputFile.getParentFile().mkdirs();
        if(!(fileExist = outputFile.exists()))
            try{fileExist = outputFile.createNewFile();}catch(Exception failed) {
                Toast.makeText(context, context.getText(R.string.failed_to_start_record), Toast.LENGTH_SHORT).show();
                recorder = null;
                return false;
            }
        if(!folderExist || !fileExist) {
            Toast.makeText(context, context.getText(R.string.failed_to_start_record), Toast.LENGTH_SHORT).show();
            recorder = null;
            return false;
        }
        recorder.setOutputFile(fileLocation);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {recorder.prepare();}catch(Exception ignore){return false;}
        lastFileRecorded = outputFile;
        recorder.start();
        return true;
    }

    public static boolean stopRecording() {
        if(recorder == null)
            return false;
        try {
            recorder.stop();
            recorder.release();
        } catch(Exception failed) {return false;}
        recorder = null;
        return true;
    }

    public static boolean canRecord() {
        return recorder == null;
    }

    public static boolean toggleRecord(Context context) {
        if(recorder == null)
            return recordToNewFile(context);
        else
            return stopRecording();
    }

    public static File[] getAllRecordings() {
        return (new File(getFolderLocation())).listFiles();
    }

    public static String getFolderLocation() {
        return Environment.getExternalStorageDirectory()+"/"+VOICE_RECORDS_FOLDER+"/";
    }

    public static boolean changeName(String newName) {
        File newNameFile = new File(lastFileRecorded.getParent()+"//"+newName);
        int iterator = 1;
        while(newNameFile.exists()) {
            newNameFile = new File(lastFileRecorded.getParent()+"//"+newName+"("+iterator+")");
            iterator++;
        }
        boolean toReturn = false;
        try {toReturn = lastFileRecorded.renameTo(newNameFile);} catch(Exception ignore){}
        return toReturn;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNameFormat() {
        String toReturn = "";
        if(forceIsraelFileNameFormat)
            toReturn += new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
        else
            toReturn += DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT, PersonalProfile.getCurrentLocale()).format(Calendar.getInstance().getTime()).replaceAll(" |,", "_");
        return toReturn;
    }

    public static String getLastFileName() {
        return lastFileRecorded.getName().split("\\.mp4")[0];
    }

}
