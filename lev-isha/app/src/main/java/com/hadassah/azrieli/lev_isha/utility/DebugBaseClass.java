package com.hadassah.azrieli.lev_isha.utility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.hadassah.azrieli.lev_isha.R;

import java.io.PrintWriter;
import java.io.StringWriter;

import static android.content.Context.CLIPBOARD_SERVICE;


public abstract class DebugBaseClass {

    public static void showPopup(final Context context, final String header, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(header);
        builder.setMessage(content);
        builder.setNeutralButton(context.getString(R.string.press_here_to_copy_this_text),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(header,content);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, context.getText(R.string.text_copied_successfully), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public static void showPopup(final Context context, final String header, final Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        final String content = sw.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(header);
        builder.setMessage(content);
        builder.setNeutralButton(context.getString(R.string.press_here_to_copy_this_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(header,content);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, context.getText(R.string.text_copied_successfully), Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

}
