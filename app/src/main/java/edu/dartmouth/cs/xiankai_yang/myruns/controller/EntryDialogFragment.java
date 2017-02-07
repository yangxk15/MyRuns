package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.util.EntryDialogType;
import lombok.AllArgsConstructor;

/**
 * Created by yangxk15 on 2/6/17.
 */

public class EntryDialogFragment extends DialogFragment {
    private static final String ENTRY_DIALOG_TYPE = "EntryDialogType";

    static EntryDialogFragment newInstance(String entryDialogType) {
        EntryDialogFragment entryDialogFragment = new EntryDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ENTRY_DIALOG_TYPE, entryDialogType);
        entryDialogFragment.setArguments(bundle);

        return entryDialogFragment;
    }


    @Override
    @TargetApi(24)
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EntryDialogType entryDialogType = EntryDialogType.fromString(
                getArguments().getString(ENTRY_DIALOG_TYPE)
        );

        switch (entryDialogType) {
            case DATE: {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity());

                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = ((ManualEntryActivity) getActivity()).mCalendar;
                        calendar.set(year, month, dayOfMonth);
                    }
                });

                return datePickerDialog;
            }
            case TIME: {
                Calendar tempCalendar = Calendar.getInstance();

                return new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = ((ManualEntryActivity) getActivity()).mCalendar;
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                            }
                        },
                        tempCalendar.get(Calendar.HOUR_OF_DAY),
                        tempCalendar.get(Calendar.MINUTE),
                        false
                );
            }
            case DURATION: {
                final EditText editText = new EditText(getActivity());
                editText.setHint("");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                return builder
                        .setTitle(entryDialogType.toString())
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ManualEntryActivity) getActivity()).mExerciseEntry.setMDuration(
                                        Integer.valueOf(editText.getText().toString())
                                );
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                .create();
            }
            case DISTANCE: {
                final EditText editText = new EditText(getActivity());
                editText.setHint("");
                editText.setInputType(
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                return builder
                        .setTitle(entryDialogType.toString())
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ManualEntryActivity) getActivity()).mExerciseEntry.setMDistance(
                                        Float.valueOf(editText.getText().toString())
                                );
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create();
            }
            case CALORIES: {
                final EditText editText = new EditText(getActivity());
                editText.setHint("");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                return builder
                        .setTitle(entryDialogType.toString())
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ManualEntryActivity) getActivity()).mExerciseEntry.setMCalorie(
                                        Integer.valueOf(editText.getText().toString())
                                );
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create();
            }
            case HEART_RATE: {
                final EditText editText = new EditText(getActivity());
                editText.setHint("");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                return builder
                        .setTitle(entryDialogType.toString())
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ManualEntryActivity) getActivity()).mExerciseEntry.setMHeartRate(
                                        Integer.valueOf(editText.getText().toString())
                                );
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create();
            }
            case COMMENT: {
                final EditText editText = new EditText(getActivity());
                editText.setHint("How did it go? Notes here.");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                return builder
                        .setTitle(entryDialogType.toString())
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ManualEntryActivity) getActivity()).mExerciseEntry.setMComment(
                                        editText.getText().toString()
                                );
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create();
            }
            default:
                return null;
        }
    }
}
