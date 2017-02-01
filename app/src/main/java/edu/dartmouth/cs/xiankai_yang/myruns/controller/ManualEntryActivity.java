package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import edu.dartmouth.cs.xiankai_yang.myruns.R;

public class ManualEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        String[] recordContentLabels = {
                "Date",
                "Time",
                "Duration",
                "Distance",
                "Calories",
                "Heart Rate",
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
                String value = (String) parent.getItemAtPosition(position);
                if (value.equals("Date")) {
                    new DatePickerDialog(ManualEntryActivity.this).show();
                } else if (value.equals("Time")) {
                    Calendar calendar = Calendar.getInstance();
                    new TimePickerDialog(ManualEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {}
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                } else {
                    EditText editText = new EditText(ManualEntryActivity.this);
                    editText.setHint("Please type in your " + value);
                    editText.setInputType(value.equals("Comment") ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_NUMBER);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntryActivity.this);
                    builder
                            .setTitle(value)
                            .setView(editText)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });
                    builder.show();
                }
            }
        });
    }

    public void onClickSaveManualEntry(View view) {
        Toast.makeText(getApplicationContext(), "Manual Entry Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onClickCancelManualEntry(View view) {
        Toast.makeText(getApplicationContext(), "Manual Entry Discarded", Toast.LENGTH_SHORT).show();
        finish();
    }
}
