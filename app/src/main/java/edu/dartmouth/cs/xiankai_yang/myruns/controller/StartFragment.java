package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FragmentPagerUtil;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ServerUtilities;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yangxk15 on 1/16/17.
 */

public class StartFragment extends Fragment implements FragmentPagerUtil {
    private static final String TAG = "StartFragment";

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
                                        : MapDisplayActivity.class
                        );
                        intent.putExtra(INPUT_TYPE,
                                InputType.fromString(inputType).ordinal());
                        intent.putExtra(ACTIVITY_TYPE,
                                ActivityType.fromString(activityType).ordinal());
                        startActivityForResult(intent, MANUAL_ENTRY_REQUEST_CODE);
                    }
                }
        );
        ((Button) v.findViewById(R.id.start_sync)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AsyncTask<Context, Void, String>() {
                            Context mContext;
                            @Override
                            protected String doInBackground(Context... contexts) {
                                mContext = contexts[0];
                                ArrayList<ExerciseEntry> entries =
                                        ExerciseEntryDbHelper.getInstance(mContext).fetchEntries();
                                JSONArray jsonArray = new JSONArray();
                                try {
                                    for (ExerciseEntry exerciseEntry : entries) {
                                        jsonArray.put(exerciseEntry.toJSONObject(mContext));
                                    }
                                } catch (JSONException e) {
                                    Log.d(TAG, e.getMessage());
                                }

                                Map<String, String> params = new HashMap<>();
                                params.put(
                                        "local_entries",
                                        jsonArray.toString()
                                );
                                try {
                                    ServerUtilities.post(
                                            "https://"
                                                    + contexts[0].getString(R.string.project_id)
                                                    + ".appspot.com/sync",
                                            params
                                    );
                                    return "Synchronization success";
                                } catch (IOException e) {
                                    Log.d(TAG, e.getMessage());
                                    return "Synchronization failure";
                                }
                            }
                            @Override
                            protected void onPostExecute(String arg0) {
                                Toast.makeText(mContext, arg0, Toast.LENGTH_SHORT).show();
                            }
                        }.execute(getActivity());
                    }
                }
        );
        return v;
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "START";
    }
}
