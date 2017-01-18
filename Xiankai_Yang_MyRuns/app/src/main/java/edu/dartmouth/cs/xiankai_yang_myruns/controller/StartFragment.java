package edu.dartmouth.cs.xiankai_yang_myruns.controller;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import edu.dartmouth.cs.xiankai_yang_myruns.R;
import edu.dartmouth.cs.xiankai_yang_myruns.util.FragmentPagerUtil;
import edu.dartmouth.cs.xiankai_yang_myruns.controller.ManualEntryActivity;

/**
 * Created by yangxk15 on 1/16/17.
 */

public class StartFragment extends Fragment implements FragmentPagerUtil {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        ((Button) v.findViewById(R.id.start_start)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner inputTypeSpinner = (Spinner) getView().findViewById(R.id.start_input_type_spinner);
                // Spinner activityTypeSpinner = (Spinner) getView().findViewById(R.id.start_activity_type_spinner);
                // More business logic will be added.
                startActivity(new Intent(
                        getActivity(),
                        inputTypeSpinner.getSelectedItem().toString().equals("Manual Entry")
                                ? ManualEntryActivity.class
                                : MapActivity.class));
            }
        });
        ((Button) v.findViewById(R.id.start_sync)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
        return v;
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "START";
    }
}
