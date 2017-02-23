package edu.dartmouth.cs.xiankai_yang.myruns.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.dartmouth.cs.xiankai_yang.myruns.util.ActivityType;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ExerciseEntryAdapter;
import edu.dartmouth.cs.xiankai_yang.myruns.util.ExerciseEntryTableColumns;
import edu.dartmouth.cs.xiankai_yang.myruns.util.InputType;
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
    private static final int L = Double.SIZE / Byte.SIZE;
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
    private ArrayList<LatLng> mLocationList; // Location list

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
                cursor.getString(cursor.getColumnIndex(_COMMENT)),
                fromLocationListByteArray(cursor.getBlob(cursor.getColumnIndex(_GPS_DATA)))
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
        contentValues.put(_GPS_DATA, toLocationListByteArray(mLocationList));
        return contentValues;
    }

    /**
     * Create a JSONObject from an ExerciseEntry
     * @return created JSONObject instance
     */
    public JSONObject toJSONObject(Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(_ID, String.valueOf(id));
        jsonObject.put(_INPUT_TYPE, InputType.values()[mInputType]);
        jsonObject.put(_ACTIVITY_TYPE, ActivityType.values()[mActivityType]);
        jsonObject.put(_DATE_TIME, mDateTime == null
                ? "" : DATE_FORMAT.format(mDateTime.getTime()));
        jsonObject.put(_DURATION, ExerciseEntryAdapter.getFormattedTime(mDuration));
        jsonObject.put(_DISTANCE, String.format("%.2f", mDistance) + " Miles");
        jsonObject.put(_AVG_PACE, mAvgPace + "");
        jsonObject.put(_AVG_SPEED, String.format("%.2f", mAvgSpeed) + " Miles/h");
        jsonObject.put(_CALORIES, mCalorie + " Calories");
        jsonObject.put(_CLIMB, String.format("%.2f", mClimb) + "");
        jsonObject.put(_HEART_RATE, mHeartRate + "");
        jsonObject.put(_COMMENT, mComment == null ? "" : mComment);
        return jsonObject;
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

    private static byte[] toLocationListByteArray(ArrayList<LatLng> locationArrayList) {
        if (locationArrayList == null) {
            return null;
        }

        byte[] bytes = new byte[locationArrayList.size() * L * 2];
        for (int i = 0; i < locationArrayList.size(); i++) {
            ByteBuffer.wrap(bytes, i * L * 2, L).putDouble(locationArrayList.get(i).latitude);
            ByteBuffer.wrap(bytes, i * L * 2 + L, L).putDouble(locationArrayList.get(i).longitude);
        }
        return bytes;
    }

    private static ArrayList<LatLng> fromLocationListByteArray(byte[] locationByteArray) {
        if (locationByteArray == null) {
            return null;
        }

        ArrayList<LatLng> arrayList = new ArrayList<>();
        for (int i = 0; i < locationByteArray.length / 2 / L; i++) {
            arrayList.add(
                    new LatLng(
                            ByteBuffer.wrap(locationByteArray, i * L * 2, L).getDouble(),
                            ByteBuffer.wrap(locationByteArray, i * L * 2 + L, L).getDouble()
                    )
            );
        }
        return arrayList;
    }
}
