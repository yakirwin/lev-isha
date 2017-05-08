package com.hadassah.azrieli.lev_isha.core;


import com.hadassah.azrieli.lev_isha.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Observable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfile;
import com.hadassah.azrieli.lev_isha.utility.PersonalProfileEntry;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PersonalProfileActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProfileAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        mRecyclerView = (RecyclerView) findViewById(R.id.personal_profile_Recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ProfileAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(this.getResources().getString(R.string.personal_profile));
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.profile_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addNewEntry();
            }
        });
    }

    class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

        private ArrayList<PersonalProfileEntry> mDataset;
        private Context context;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView summary;
            ImageButton delete;
            ImageView icon;
            RelativeLayout layout;
            ViewHolder(RelativeLayout layout) {
                super(layout);
                this.layout = layout;
                title = (TextView)layout.findViewById(R.id.entry_title);
                summary = (TextView)layout.findViewById(R.id.entry_summary);
                delete = (ImageButton)layout.findViewById(R.id.entry_delete);
                icon = (ImageView)layout.findViewById(R.id.entry_icon);
                layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        editEntry(view,(int)view.getTag());
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        deleteEntry(view,(int)view.getTag());
                    }
                });
            }
        }

        ProfileAdapter(Context context) {
            this.context = context;
            mDataset = PersonalProfile.getInstance(context).getEntriesCopy();
        }

        public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_view_entry, parent, false);
            ViewHolder vh = new ViewHolder(layout);
            return vh;
        }

        public PersonalProfileEntry getItem(int position) {
            return mDataset.get(position);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            PersonalProfileEntry entry = mDataset.get(position);
            holder.title.setText(entry.getName());
            holder.summary.setText((entry.getValue() == null) ? context.getResources().getText(R.string.undefined) : entry.getValue());
            if(entry.isEssential())
                holder.delete.setVisibility(View.INVISIBLE);
            else
                holder.delete.setVisibility(View.VISIBLE);
            if(entry.getInputType() == PersonalProfileEntry.DATE)
                holder.icon.setBackgroundResource(R.drawable.icon_date);
            else if(entry.getInputType() == PersonalProfileEntry.REAL_NUMBERS)
                holder.icon.setBackgroundResource(R.drawable.icon_number);
            else
                holder.icon.setBackgroundResource(R.drawable.icon_text);
            holder.layout.setTag(position);
            holder.delete.setTag(position);
        }

        public int getItemCount() {
            return mDataset.size();
        }

    }



    private void addNewEntry() {
        if(mAdapter == null)
            return;
        final Resources res = this.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(res.getText(R.string.add_new_property));
        builder.setNegativeButton(R.string.cancel,null);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.text_input, null);
        builder.setView(dialogView);
        final EditText editText = (EditText)dialogView.findViewById(R.id.text_input_text_box);
        final RadioGroup radioGroup = (RadioGroup)dialogView.findViewById(R.id.text_input_radio_group);
        RadioButton rb1 = new RadioButton(this);
        rb1.setText(res.getText(R.string.plain_text));
        radioGroup.addView(rb1);
        rb1.setPadding(10,0,10,0);
        rb1.setChecked(true);
        RadioButton rb2 = new RadioButton(this);
        rb2.setText(res.getText(R.string.real_numbers));
        radioGroup.addView(rb2);
        rb2.setPadding(10,0,10,0);
        RadioButton rb3 = new RadioButton(this);
        rb3.setText(res.getText(R.string.date));
        radioGroup.addView(rb3);
        rb3.setPadding(10,0,10,0);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(editText.getText().length() == 0)
                    return;
                int type;
                type = radioGroup.getCheckedRadioButtonId();
                String rawType = ((RadioButton)dialogView.findViewById(type)).getText().toString();
                if(rawType.equals(res.getText(R.string.real_numbers)))
                    type = PersonalProfileEntry.REAL_NUMBERS;
                else if(rawType.equals(res.getText(R.string.date)))
                    type = PersonalProfileEntry.DATE;
                else
                    type = PersonalProfileEntry.PLAIN_TEXT;
                PersonalProfile profile = PersonalProfile.getInstance(PersonalProfileActivity.this);
                PersonalProfileEntry entry = profile.addNewField(editText.getText().toString(),null,type);
                if(entry != null) {
                    mAdapter.mDataset = PersonalProfile.getInstance(PersonalProfileActivity.this).getEntriesCopy();
                    mAdapter.notifyItemInserted(mAdapter.mDataset.size());
                    profile.commitChanges(PersonalProfileActivity.this);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


       private boolean deleteEntry(View view, final int index) {
        if(mAdapter == null)
            return false;
           final PersonalProfileEntry toDelete;
        try {
            toDelete = mAdapter.getItem(index);
        }catch(Exception ignore){return false;}
        if(toDelete == null || toDelete.isEssential())
            return false;
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalProfileActivity.this);
        builder.setTitle(toDelete.getName());
        builder.setMessage(PersonalProfileActivity.this.getResources().getText(R.string.delete_personal_profile_entry_explain)+" "+toDelete.getName()+"?");
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PersonalProfile profile = PersonalProfile.getInstance(PersonalProfileActivity.this);
                if(profile.deleteFieldByEntry(toDelete) != null) {
                    mAdapter.mDataset = PersonalProfile.getInstance(PersonalProfileActivity.this).getEntriesCopy();
                    mAdapter.notifyItemRemoved(index);
                    mAdapter.notifyItemRangeChanged(index,mAdapter.getItemCount());
                    profile.commitChanges(PersonalProfileActivity.this);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private void editEntry(final View view, final int index) {
        if(mAdapter == null)
            return;
        final PersonalProfileEntry toEdit = mAdapter.getItem(index);
        if(toEdit.getName().equals(this.getResources().getText(R.string.bmi)))
            return;
        final PersonalProfile profile = PersonalProfile.getInstance(PersonalProfileActivity.this);
        if(toEdit.getInputType() == PersonalProfileEntry.DATE) {
            Calendar currentTime = Calendar.getInstance();
            int year, month, day;
            Calendar date = null;
            final DateFormat df = DateFormat.getDateInstance();
            if(toEdit.getValue() != null) {
                String value = toEdit.getValue();
                try {
                    date = Calendar.getInstance();
                    date.setTime(df.parse(value));
                } catch(Exception exp){}
            }
            year = (date == null) ? currentTime.get(Calendar.YEAR) : date.get(Calendar.YEAR);
            month = (date == null) ? currentTime.get(Calendar.MONTH) : date.get(Calendar.MONTH);
            day = (date == null) ? currentTime.get(Calendar.DAY_OF_MONTH) : date.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(PersonalProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(i,i1,i2);
                    String format = df.format(calendar.getTime());
                    toEdit.setValue(format);
                    TextView summary = (TextView)view.findViewById(R.id.entry_summary);
                    summary.setText(format);
                    mAdapter.mDataset = PersonalProfile.getInstance(PersonalProfileActivity.this).getEntriesCopy();
                    mAdapter.notifyItemChanged(index);
                    profile.commitChanges(PersonalProfileActivity.this);
                }
            }, year, month, day).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(toEdit.getName());
            builder.setNegativeButton(R.string.cancel,null);
            View dialogView = this.getLayoutInflater().inflate(R.layout.text_input, null);
            builder.setView(dialogView);
            final EditText editText = (EditText)dialogView.findViewById(R.id.text_input_text_box);
            if(toEdit.getValue() != null)
                editText.setText(toEdit.getValue());
            editText.setInputType(toEdit.getInputType());
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(editText.getText().length() == 0)
                        return;
                    toEdit.setValue(editText.getText().toString());
                    TextView summary = (TextView)view.findViewById(R.id.entry_summary);
                    summary.setText(editText.getText().toString());
                    if(calculateBMI())
                    {
                        mAdapter.mDataset = PersonalProfile.getInstance(PersonalProfileActivity.this).getEntriesCopy();
                        mAdapter.notifyItemChanged(profile.findEntryByName(PersonalProfileActivity.this.getText(R.string.bmi).toString()).getIndex());
                    }
                    else
                        mAdapter.mDataset = PersonalProfile.getInstance(PersonalProfileActivity.this).getEntriesCopy();
                    mAdapter.notifyItemChanged(index);
                    profile.commitChanges(PersonalProfileActivity.this);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean calculateBMI() {
        PersonalProfile profile = PersonalProfile.getInstance(this);
        PersonalProfileEntry heightEntry = profile.findEntryByName(this.getResources().getText(R.string.height).toString());
        PersonalProfileEntry weightEntry = profile.findEntryByName(this.getResources().getText(R.string.weight).toString());
        double weight,height;
        try {
            weight = Double.parseDouble(weightEntry.getValue());
            height = Double.parseDouble(heightEntry.getValue());
        }catch(Exception ignore){return false;}
        String resultBMI = new DecimalFormat("#.##").format(weight/(height*height));
        PersonalProfileEntry BMIEntry = profile.findEntryByName(this.getResources().getText(R.string.bmi).toString());
        BMIEntry.setValue(resultBMI);
        profile.commitChanges(PersonalProfileActivity.this);
        return true;
    }



}