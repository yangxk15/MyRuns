package edu.dartmouth.cs.xiankai_yang.myruns.util;

import android.provider.BaseColumns;

/**
 * Created by yangxk15 on 2/1/17.
 */

public interface ExerciseEntryTableColumns extends BaseColumns {
    String _INPUT_TYPE = "_input_type";
    String _ACTIVITY_TYPE = "_activity_type";
    String _DATE_TIME = "_date_time";
    String _DURATION = "_duration";
    String _DISTANCE = "_distance";
    String _AVG_PACE = "_avg_pace";
    String _AVG_SPEED = "_avg_speed";
    String _CALORIES = "_calories";
    String _CLIMB = "_climb";
    String _HEART_RATE = "_heart_rate";
    String _COMMENT = "_comment";
    String _PRIVACY = "_privacy";
    String _GPS_DATA = "_gps_data";
}
