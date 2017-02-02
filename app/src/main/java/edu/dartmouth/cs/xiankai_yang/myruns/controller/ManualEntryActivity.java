package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;
import edu.dartmouth.cs.xiankai_yang.myruns.util.Constants;

public class ManualEntryActivity extends AppCompatActivity {
    private static final String TAG = "ManualEntryActivity";

    int mInputType = -1;
    int mActivityType = -1;
    Calendar mCalendar = Calendar.getInstance();
    String mDuration = null;
    String mDistance = null;
    String mCalories = null;
    String mHeartRate = null;
    String mComment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        mInputType = getIntent().getIntExtra(StartFragment.INPUT_TYPE, mInputType);
        mActivityType = getIntent().getIntExtra(StartFragment.ACTIVITY_TYPE, mActivityType);

        String[] recordContentLabels = {
                "Date",
                "Time",
                "Duration",
                "Distance",
                "Calories",
                "HeartRate",
                "Comment",
        };

        ListView listView = (ListView) findViewById(R.id.manual_entry_record_content);
        listView.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordContentLabels)
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            @TargetApi(24)
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String value = (String) parent.getItemAtPosition(position);

                if (value.equals("Date"))
                {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ManualEntryActivity.this);

                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            mCalendar.set(year, month, dayOfMonth);
                        }
                    });

                    datePickerDialog.show();
                }
                else if (value.equals("Time"))
                {
                    Calendar tempCalendar = Calendar.getInstance();

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            ManualEntryActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    mCalendar.set(Calendar.MINUTE, minute);
                                    mCalendar.set(Calendar.SECOND, 0);
                                }
                            },
                            tempCalendar.get(Calendar.HOUR_OF_DAY),
                            tempCalendar.get(Calendar.MINUTE),
                            false
                    );

                    timePickerDialog.show();
                }
                else
                {

                    Class variableType = null;
                    try {
                        variableType = ExerciseEntry.class.getDeclaredField("m" + value).getType();
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                    final EditText editText = new EditText(ManualEntryActivity.this);
                    editText.setHint("Please type in your " + value);
                    editText.setInputType(
                            variableType == String.class ? InputType.TYPE_CLASS_TEXT
                                    : variableType == Float.class ? InputType.TYPE_NUMBER_FLAG_DECIMAL
                                    : InputType.TYPE_CLASS_NUMBER
                    );

                    AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntryActivity.this);

                    builder
                            .setTitle(value)
                            .setView(editText)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (value.equals("Duration")) {
                                        mDuration = editText.getText().toString();
                                    } else if (value.equals("Distance")) {
                                        mDistance = editText.getText().toString();
                                    } else if (value.equals("Calories")) {
                                        mCalories = editText.getText().toString();
                                    } else if (value.equals("HeartRate")) {
                                        mHeartRate = editText.getText().toString();
                                    } else if (value.equals("Comment")) {
                                        mComment = editText.getText().toString();
                                    }
                                }
                            })
                            .setNegativeButton("CANCEL", null);

                    builder.show();
                }
            }
        });
    }

    public void onClickSaveManualEntry(View view) {
        ExerciseEntry exerciseEntry = new ExerciseEntry(
                null,
                mInputType,
                mActivityType,
                mCalendar,
                mDuration == null ? 0 : Integer.valueOf(mDuration),
                mDistance == null ? 0 : Float.valueOf(mDistance),
                -1,
                -1,
                mCalories == null ? 0 : Integer.valueOf(mCalories),
                -1,
                mHeartRate == null ? 0 : Integer.valueOf(mHeartRate),
                mComment
        );
        long id = new ExerciseEntryDbHelper(this).insertEntry(exerciseEntry);
        Toast.makeText(getApplicationContext(), "Entry #" + id + " Saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, new Intent());
        finish();
    }

    public void onClickCancelManualEntry(View view) {
        Toast.makeText(getApplicationContext(), "Manual Entry Discarded", Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}
