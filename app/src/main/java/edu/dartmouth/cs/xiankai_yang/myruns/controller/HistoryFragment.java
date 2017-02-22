package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntryDbHelper;
import edu.dartmouth.cs.xiankai_yang.myruns.util.FragmentPagerUtil;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yangxk15 on 1/16/17.
 */

public class HistoryFragment extends ListFragment implements FragmentPagerUtil {
    public static final String EXERCISE_ENTRY = "Exercise_Entry";

    private static final int ENTRY_DETAIL_REQUEST_CODE = 0;

    ArrayList<ExerciseEntry> exerciseEntries = null;
    ExerciseEntryAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        exerciseEntries = ExerciseEntryDbHelper.getInstance(getActivity()).fetchEntries();
        adapter = new ExerciseEntryAdapter(getActivity(), exerciseEntries);
        setListAdapter(adapter);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ExerciseEntry exerciseEntry = exerciseEntries.get(position);
        Intent intent = new Intent(
                getActivity(),
                exerciseEntry.getMInputType() == InputType.MANUAL_ENTRY.ordinal()
                        ? EntryDetailActivity.class
                        : MapDisplayActivity.class
        );
        intent.putExtra(EXERCISE_ENTRY, new Gson().toJson(exerciseEntry));
        startActivityForResult(intent, ENTRY_DETAIL_REQUEST_CODE);
    }

    public void reload() {
        if (exerciseEntries == null) {
            return;
        }

        new AsyncTask<Context, Void, ArrayList<ExerciseEntry>>() {
            @Override
            protected ArrayList<ExerciseEntry> doInBackground(Context... contexts) {
                return ExerciseEntryDbHelper.getInstance(contexts[0]).fetchEntries();
            }
            @Override
            protected void onPostExecute(ArrayList<ExerciseEntry> newEntries) {
                exerciseEntries.clear();
                exerciseEntries.addAll(newEntries);
                adapter.notifyDataSetChanged();
            }
        }.execute(getActivity());
    }

    @Override
    public CharSequence getFragmentPagerTitle() {
        return "HISTORY";
    }
}
