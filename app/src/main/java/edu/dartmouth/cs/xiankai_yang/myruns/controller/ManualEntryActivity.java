package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;
import edu.dartmouth.cs.xiankai_yang.myruns.util.EntryDialogType;

public class ManualEntryActivity extends AppCompatActivity {
    private static final String TAG = "ManualEntryActivity";

    ExerciseEntry mExerciseEntry = new ExerciseEntry();
    Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        mExerciseEntry.setMInputType(getIntent().getIntExtra(StartFragment.INPUT_TYPE,
                mExerciseEntry.getMInputType()));
        mExerciseEntry.setMActivityType(getIntent().getIntExtra(StartFragment.ACTIVITY_TYPE,
                mExerciseEntry.getMActivityType()));

        ListView listView = (ListView) findViewById(R.id.manual_entry_record_content);
        listView.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, EntryDialogType.getTypes())
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String entryDialogType = (String) parent.getItemAtPosition(position);
                EntryDialogFragment.newInstance(entryDialogType)
                        .show(getFragmentManager(), entryDialogType);
            }
        });
    }

    public void onClickSaveManualEntry(View view) {
        mExerciseEntry.setMDateTime(mCalendar);
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... arg0) {
                return ExerciseEntryDbHelper.getInstance(ManualEntryActivity.this)
                        .insertEntry(mExerciseEntry);
            }
            @Override
            protected void onPostExecute(Long id) {
                Toast.makeText(getApplicationContext(),
                        "Entry #" + id + " Saved", Toast.LENGTH_SHORT).show();
            }
        }.execute();
        setResult(RESULT_OK, new Intent());
        finish();
    }

    public void onClickCancelManualEntry(View view) {
        if (view != null) {
            Toast.makeText(getApplicationContext(),
                    "Manual Entry Discarded", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onBackPressed() {
        onClickCancelManualEntry(null);
    }

}
