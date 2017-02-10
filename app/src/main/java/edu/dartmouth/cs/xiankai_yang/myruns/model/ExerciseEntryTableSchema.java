package edu.dartmouth.cs.xiankai_yang.myruns.model;

import java.util.HashMap;
import java.util.Map;

import edu.dartmouth.cs.xiankai_yang.myruns.util.ExerciseEntryTableColumns;

/**
 * Created by yangxk15 on 1/31/17.
 */

public class ExerciseEntryTableSchema implements ExerciseEntryTableColumns {
    public static final String TABLE_NAME = "_exercise_entry";
    public static final String PK = _ID;
    public static final String[] PROJECTION = new String[] {
            _ID,
            _INPUT_TYPE,
            _ACTIVITY_TYPE,
            _DATE_TIME,
            _DURATION,
            _DISTANCE,
            _AVG_PACE,
            _AVG_SPEED,
            _CALORIES,
            _CLIMB,
            _HEART_RATE,
            _COMMENT,
            _GPS_DATA
    };
    public static final HashMap<String, String> _COLUMN_TYPE = new HashMap<>();

    private static final String INTEGER = "INTEGER";
    private static final String FLOAT = "FLOAT";
    private static final String TEXT = "TEXT";
    private static final String BLOB = "BLOB";
    private static final String DATETIME = "DATETIME";
    private static final String NOT_NULL = " NOT NULL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";

    static {
        _COLUMN_TYPE.put(_ID, INTEGER + PRIMARY_KEY + AUTOINCREMENT);
        _COLUMN_TYPE.put(_INPUT_TYPE, INTEGER + NOT_NULL);
        _COLUMN_TYPE.put(_ACTIVITY_TYPE, INTEGER + NOT_NULL);
        _COLUMN_TYPE.put(_DATE_TIME, DATETIME + NOT_NULL);
        _COLUMN_TYPE.put(_DURATION, INTEGER + NOT_NULL);
        _COLUMN_TYPE.put(_DISTANCE, FLOAT);
        _COLUMN_TYPE.put(_AVG_PACE, FLOAT);
        _COLUMN_TYPE.put(_AVG_SPEED, FLOAT);
        _COLUMN_TYPE.put(_CALORIES, INTEGER);
        _COLUMN_TYPE.put(_CLIMB, FLOAT);
        _COLUMN_TYPE.put(_HEART_RATE, INTEGER);
        _COLUMN_TYPE.put(_COMMENT, TEXT);
        _COLUMN_TYPE.put(_PRIVACY, INTEGER);
        _COLUMN_TYPE.put(_GPS_DATA, BLOB);
    }

    public static String getSchema() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : _COLUMN_TYPE.entrySet()) {
            stringBuilder.append(entry.getKey() + " " + entry.getValue() + ",");
        }
        String schema = stringBuilder.toString();
        return "(" + schema.substring(0, schema.length() - 1) + ")";
    }

}
