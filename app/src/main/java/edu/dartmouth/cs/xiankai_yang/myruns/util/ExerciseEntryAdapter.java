package edu.dartmouth.cs.xiankai_yang.myruns.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.ExerciseEntry;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.Constants;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;

/**
 * Created by yangxk15 on 2/1/17.
 */

public class ExerciseEntryAdapter extends ArrayAdapter<ExerciseEntry> {
    public static final String DEFAULT_UNIT_PREFERENCE = "Imperial (Miles)";

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
                        getFormattedTime(exerciseEntry.getMDuration())
        );

        return convertView;
    }

    /**
     * Get the distance according the current unit preference.
     * @param distance
     * @return
     */
    public static String getDistanceByUnitPreference(float distance, Context context) {
        boolean isNegative = false;
        if (distance < 0) {
            distance = -distance;
            isNegative = true;
        }

        String d = isDefaultUnitPreference(context)
                ? String.format("%.2f", distance)
                : String.format("%.2f", distance / (float) Constants.MILES_PER_KILOMETER);

        if (d.equals("0.00")) {
            d = "0";
            isNegative = false;
        }

        return (isNegative ? "-" : "")
                + d + (isDefaultUnitPreference(context) ? " Miles" : " Kilometers");
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
    public static String getFormattedTime(int secs) {
        StringBuilder stringBuilder = new StringBuilder();

        if (secs / 60 != 0) {
            stringBuilder.append(secs / 60);
            stringBuilder.append(" min ");
        }

        if (secs % 60 != 0) {
            stringBuilder.append(secs % 60);
            stringBuilder.append(" sec ");
        }

        if (stringBuilder.toString().isEmpty()) {
            return "0 sec ";
        }

        return stringBuilder.toString();
    }

    public static String getFormattedString(Date date) {
        return new SimpleDateFormat("HH:mm:ss MMM dd yyyy").format(date);
    }
}
