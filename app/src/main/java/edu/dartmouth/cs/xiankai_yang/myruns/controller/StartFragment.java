package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FragmentPagerUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yangxk15 on 1/16/17.
 */

public class StartFragment extends Fragment implements FragmentPagerUtil {
    public static final String INPUT_TYPE = "input_type";
    public static final String ACTIVITY_TYPE = "activity_type";

    private static final int MANUAL_ENTRY_REQUEST_CODE = 0;
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        final Spinner inputTypeSpinner =
                (Spinner) v.findViewById(R.id.start_input_type_spinner);
        final Spinner activityTypeSpinner =
                (Spinner) v.findViewById(R.id.start_activity_type_spinner);
        // Populate the spinners
        inputTypeSpinner.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                InputType.getTypes()));
        activityTypeSpinner.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                ActivityType.getTypes()));
        ((Button) v.findViewById(R.id.start_start)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputType = inputTypeSpinner.getSelectedItem().toString();
                        String activityType = activityTypeSpinner.getSelectedItem().toString();
                        Intent intent = new Intent(
                                getActivity(),
                                inputType.equals(InputType.MANUAL_ENTRY.toString())
                                        ? ManualEntryActivity.class
                                        : MapActivity.class
                        );
                        intent.putExtra(INPUT_TYPE, InputType.fromString(inputType).ordinal());
                        intent.putExtra(ACTIVITY_TYPE, ActivityType.fromString(activityType).ordinal());
                        startActivityForResult(intent, MANUAL_ENTRY_REQUEST_CODE);
                    }
        });
        ((Button) v.findViewById(R.id.start_sync)).setOnClickListener(null);
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MANUAL_ENTRY_REQUEST_CODE) {
                ((MainActivity) getActivity()).getMHistoryFragment().reload();
            }
        }
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "START";
    }
}
