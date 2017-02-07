package edu.dartmouth.cs.xiankai_yang.myruns.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.dartmouth.cs.xiankai_yang.myruns.util.ExerciseEntryTableColumns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yangxk15 on 1/31/17.
 */

@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Data
public class ExerciseEntry implements ExerciseEntryTableColumns {
    private static final String TAG = "ExerciseEntry";
    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private Long id;
    private int mInputType;  // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Calendar mDateTime;    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private float mDistance;      // Distance traveled. Either in meters or feet.
    private float mAvgPace;       // Average pace
    private float mAvgSpeed;     // Average speed
    private int mCalorie;        // Calories burnt
    private float mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;       // Heart rate
    private String mComment;       // Comments
    //private ArrayList<LatLng> mLocationList; // Location list

    /**
     * Create a new ExerciseEntry instance from the current cursor
     * @param cursor
     * @return created ExerciseEntry instance
     */
    public static ExerciseEntry fromCursor(Cursor cursor) {
        return new ExerciseEntry(
                cursor.getLong(cursor.getColumnIndex(_ID)),
                cursor.getInt(cursor.getColumnIndex(_INPUT_TYPE)),
                cursor.getInt(cursor.getColumnIndex(_ACTIVITY_TYPE)),
                getCalendarFromString(cursor.getString(cursor.getColumnIndex(_DATE_TIME))),
                cursor.getInt(cursor.getColumnIndex(_DURATION)),
                cursor.getFloat(cursor.getColumnIndex(_DISTANCE)),
                cursor.getFloat(cursor.getColumnIndex(_AVG_PACE)),
                cursor.getFloat(cursor.getColumnIndex(_AVG_SPEED)),
                cursor.getInt(cursor.getColumnIndex(_CALORIES)),
                cursor.getFloat(cursor.getColumnIndex(_CLIMB)),
                cursor.getInt(cursor.getColumnIndex(_HEART_RATE)),
                cursor.getString(cursor.getColumnIndex(_COMMENT))
        );
    }

    /**
     * Create a new ContentValues instance with attributes of the Exercise Entry.
     * @return created ContentValues instance
     */
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(_INPUT_TYPE, mInputType);
        contentValues.put(_ACTIVITY_TYPE, mActivityType);
        contentValues.put(_DATE_TIME, mDateTime == null
                ? null : DATE_FORMAT.format(mDateTime.getTime()));
        contentValues.put(_DURATION, mDuration);
        contentValues.put(_DISTANCE, mDistance);
        contentValues.put(_AVG_PACE, mAvgPace);
        contentValues.put(_AVG_SPEED, mAvgSpeed);
        contentValues.put(_CALORIES, mCalorie);
        contentValues.put(_CLIMB, mClimb);
        contentValues.put(_HEART_RATE, mHeartRate);
        contentValues.put(_COMMENT, mComment);
        return contentValues;
    }


    /**
     * Get a Calendar instance from a date string. Return the current calendar instance if failed.
     * @param datetime
     * @return created Calendar instance
     */
    private static Calendar getCalendarFromString(String datetime) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = DATE_FORMAT.parse(datetime);
            calendar.setTime(date);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            return calendar;
        }
    }
}
