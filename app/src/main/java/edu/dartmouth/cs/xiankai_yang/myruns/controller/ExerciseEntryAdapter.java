package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.model.InputType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.Constants;

/**
 * Created by yangxk15 on 2/1/17.
 */

public class ExerciseEntryAdapter extends ArrayAdapter<ExerciseEntry> {
    public static final String DEFAULT_UNIT_PREFERENCE = "Imperial (Miles)";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    public ExerciseEntryAdapter(Context context, ArrayList<ExerciseEntry> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ExerciseEntry exerciseEntry = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.exercise_entry_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.entry_title)).setText(
                InputType.values()[(exerciseEntry.getMInputType())].toString() + ": "
                        + ActivityType.values()[exerciseEntry.getMActivityType()].toString() + ", "
                        + getFormattedString(exerciseEntry.getMDateTime().getTime())
        );
        ((TextView) convertView.findViewById(R.id.entry_details)).setText(
                getDistanceByUnitPreference(exerciseEntry.getMDistance(), getContext()) + ", " +
                        String.valueOf(exerciseEntry.getMDuration()) + " secs"
        );

        return convertView;
    }

    /**
     * Get the distance according the current unit preference.
     * @param distance
     * @return
     */
    public static String getDistanceByUnitPreference(float distance, Context context) {
        if (distance == 0) {
            return isDefaultUnitPreference(context) ? "0 Miles" : "0 Kilometers";
        }
        return isDefaultUnitPreference(context)
                ? DECIMAL_FORMAT.format(distance) + " Miles"
                : DECIMAL_FORMAT.format(distance
                / (float) Constants.MILES_PER_KILOMETER) + " Kilometers";
    }

    /**
     * Find out whether current unit preference is default KM.
     * @return
     */
    public static boolean isDefaultUnitPreference(Context context) {
        SharedPreferences myPreference =
                PreferenceManager.getDefaultSharedPreferences(context);
        String unitPreference = myPreference.getString(
                context.getString(R.string.settings_unit_preference),
                DEFAULT_UNIT_PREFERENCE
        );
        return unitPreference.equals(DEFAULT_UNIT_PREFERENCE);
    }

    public static String getFormattedString(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date) + " "
                + new SimpleDateFormat("MMM").format(date) + " "
                + new SimpleDateFormat("dd").format(date) + " "
                + new SimpleDateFormat("yyyy").format(date);
    }
}
