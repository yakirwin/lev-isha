package com.hadassah.azrieli.lev_isha.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hadassah.azrieli.lev_isha.R;
import com.hadassah.azrieli.lev_isha.utility.ContextWrapper;
import com.hadassah.azrieli.lev_isha.utility.GeneralPurposeService;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;
import com.hadassah.azrieli.lev_isha.utility.VoiceRecorder;

import java.io.File;
import java.io.FileInputStream;

public class RecordsActivity extends AppCompatActivity {

    private recordsAdapter mAdapter;
    private recordsAdapter.ViewHolder currentlyPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(R.string.doctor_records_label);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        File[] fileList = VoiceRecorder.getAllRecordings();
        if(fileList == null || fileList.length == 0) {
            NestedScrollView nScroll = (NestedScrollView)this.findViewById(R.id.recordings_scroll_view);
            nScroll.setVisibility(View.GONE);
            return;
        }
        TextView errorText = (TextView)this.findViewById(R.id.no_recordings_found);
        errorText.setVisibility(View.GONE);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.doctor_records_Recycler_view);
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new recordsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        if(!GeneralPurposeService.isServiceRunning())
            this.startService(new Intent(this,GeneralPurposeService.class));
    }

    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase,  PersonalProfile.getCurrentLocale());
        super.attachBaseContext(context);
    }

    class recordsAdapter extends RecyclerView.Adapter<recordsAdapter.ViewHolder> {

        private File[] mDataset;
        private Context adapterContext;

        private recordsAdapter(Context context) {
            mDataset = VoiceRecorder.getAllRecordings();
            adapterContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.records_list_entry, parent, false);
            return new ViewHolder(layout);
        }

        private File getItem(int position) {
            return mDataset[position];
        }


        public void onBindViewHolder(ViewHolder holder, int position) {
            File entry = mDataset[position];
            holder.title.setText(entry.getName());
        }

        public int getItemCount() {
            return mDataset.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView title;
            private ImageButton delete;
            private ImageButton share;
            private ImageButton play;
            private MediaPlayer mediaPlayer;
            private Context context;

            ViewHolder(LinearLayout layout) {
                super(layout);
                title = layout.findViewById(R.id.entry_title);
                play = layout.findViewById(R.id.entry_play);
                share = layout.findViewById(R.id.entry_share);
                delete = layout.findViewById(R.id.entry_delete);
                context = adapterContext;
                play.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        title.setTextColor(Color.RED);
                        if(currentlyPlaying == ViewHolder.this)
                            if(mediaPlayer.isPlaying())
                                pausePlayingRecord();
                            else
                                resumePlayingRecord();
                        else {
                            if (currentlyPlaying != null)
                            {
                                currentlyPlaying.title.setTextColor(Color.BLACK);
                                title.setTextColor(Color.RED);
                                stopPlayingRecord();
                            }
                            currentlyPlaying = ViewHolder.this;
                            startPlayingRecord();
                        }
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent .setType("vnd.android.cursor.dir/email");
                        intent.putExtra(Intent.EXTRA_SUBJECT,title.getText());
                        intent.putExtra(Intent.EXTRA_TEXT,title.getText());
                        //Uri attach = Uri.fromFile(new File(VoiceRecorder.getFolderLocation()+title.getText()));
                        Uri attach = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",new File(VoiceRecorder.getFolderLocation()+title.getText()));
                        intent .putExtra(Intent.EXTRA_STREAM,attach);
                        startActivity(Intent.createChooser(intent,getString(R.string.share_chooser_header)));
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if(currentlyPlaying != null) {
                            currentlyPlaying.title.setTextColor(Color.BLACK);
                            currentlyPlaying.mediaPlayer.stop();
                            currentlyPlaying.play.setBackgroundResource(R.drawable.play_icon);
                            currentlyPlaying = null;
                        }
                        deleteEntry(getAdapterPosition(),ViewHolder.this);
                    }
                });
            }
        }

    }

    public void startPlayingRecord() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(VoiceRecorder.getFolderLocation()+
                    (currentlyPlaying.title.getText().toString()));
            mediaPlayer.setDataSource(inputStream.getFD());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            currentlyPlaying.play.setBackgroundResource(R.drawable.icon_pause);
            currentlyPlaying.mediaPlayer = mediaPlayer;
        }catch(Exception ignore){return;}
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                currentlyPlaying.play.setBackgroundResource(R.drawable.play_icon);
                currentlyPlaying.title.setTextColor(Color.BLACK);
                currentlyPlaying = null;
            }
        });
        mediaPlayer.start();
    }

    public void pausePlayingRecord() {
        currentlyPlaying.mediaPlayer.pause();
        currentlyPlaying.play.setBackgroundResource(R.drawable.play_icon);
    }

    public void resumePlayingRecord() {
        currentlyPlaying.mediaPlayer.start();
        currentlyPlaying.play.setBackgroundResource(R.drawable.icon_pause);
    }

    public void stopPlayingRecord() {
        MediaPlayer mediaPlayer = currentlyPlaying.mediaPlayer;
        currentlyPlaying.play.setBackgroundResource(R.drawable.play_icon);
        mediaPlayer.stop();
        mediaPlayer.release();
        currentlyPlaying = null;
    }

    private boolean deleteEntry(final int index, final recordsAdapter.ViewHolder deleting) {
        if(mAdapter == null)
            return false;
        final File toDelete;
        try { toDelete = mAdapter.getItem(index);} catch(Exception ignore){ return false; }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.delete));
        builder.setMessage(getResources().getText(R.string.delete_record_body)+" "+toDelete.getName()+"?");
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if(toDelete.delete()) {
                    if(currentlyPlaying != null && currentlyPlaying.equals(deleting)) {
                        currentlyPlaying.title.setTextColor(Color.BLACK);
                        currentlyPlaying = null;
                    }
                    mAdapter.mDataset = VoiceRecorder.getAllRecordings();
                    mAdapter.notifyItemRemoved(index);
                    mAdapter.notifyItemRangeChanged(index,mAdapter.getItemCount());
                    if(mAdapter.getItemCount() == 0) {
                        RecordsActivity.this.findViewById(R.id.recordings_scroll_view).setVisibility(View.GONE);
                        RecordsActivity.this.findViewById(R.id.no_recordings_found).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

}
