package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;

public class EntryDetailActivity extends AppCompatActivity {
    ExerciseEntry mExerciseEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);

        mExerciseEntry = (ExerciseEntry) new Gson().fromJson(getIntent().getExtras()
                .getString(HistoryFragment.EXERCISE_ENTRY), ExerciseEntry.class);

        ((EditText) findViewById(R.id.entry_detail_input_type))
                .setText(InputType.values()[(mExerciseEntry.getMInputType())].toString());
        ((EditText) findViewById(R.id.entry_detail_activity_type))
                .setText(ActivityType.values()[mExerciseEntry.getMActivityType()].toString());
        ((EditText) findViewById(R.id.entry_detail_date_and_time))
                .setText(ExerciseEntryAdapter.getFormattedString(mExerciseEntry
                        .getMDateTime().getTime()));
        ((EditText) findViewById(R.id.entry_detail_duration))
                .setText(ExerciseEntryAdapter.getFormattedTime(mExerciseEntry.getMDuration()));
        ((EditText) findViewById(R.id.entry_detail_distance))
                .setText(ExerciseEntryAdapter.getDistanceByUnitPreference(
                        mExerciseEntry.getMDistance(), this
                ));
        ((EditText) findViewById(R.id.entry_detail_calories))
                .setText(mExerciseEntry.getMCalorie() + " cals");
        ((EditText) findViewById(R.id.entry_detail_heart_rate))
                .setText(mExerciseEntry.getMHeartRate() + " bpm");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.entry_detail_delete:
                ExerciseEntryDbHelper.getInstance(this).removeEntry(mExerciseEntry.getId());
                setResult(RESULT_OK, new Intent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
