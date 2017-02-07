package edu.dartmouth.cs.xiankai_yang.myruns.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.xiankai_yang.myruns.util.ExerciseEntryTableColumns;

/**
 * Created by yangxk15 on 1/31/17.
 */

public class ExerciseEntryDbHelper extends SQLiteOpenHelper implements ExerciseEntryTableColumns {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ExerciseEntry.db";

    private static ExerciseEntryDbHelper instance;

    private static final String TAG = "ExerciseEntryDbHelper";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ExerciseEntryTableSchema.TABLE_NAME +
                    " " + ExerciseEntryTableSchema.getSchema();
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ExerciseEntryTableSchema.TABLE_NAME;

    private ExerciseEntryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static ExerciseEntryDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ExerciseEntryDbHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    // Insert a item given each column value
    public long insertEntry(ExerciseEntry entry) {
        Log.d(TAG, "insert entry " + entry);
        return getWritableDatabase()
                .insert(ExerciseEntryTableSchema.TABLE_NAME, null, entry.getContentValues());
    }

    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        getWritableDatabase().delete(
                ExerciseEntryTableSchema.TABLE_NAME,
                ExerciseEntryTableSchema.PK + " = " + String.valueOf(rowIndex),
                null
        );
    }

    // Query a specific entry by its index.
    public ExerciseEntry fetchEntryByIndex(long rowId) {

        Cursor cursor = getReadableDatabase().query(
                ExerciseEntryTableSchema.TABLE_NAME,
                ExerciseEntryTableSchema.PROJECTION,
                ExerciseEntryTableSchema.PK,
                new String[]{String.valueOf(rowId)},
                null,
                null,
                null
        );

        cursor.moveToFirst();
        ExerciseEntry exerciseEntry = ExerciseEntry.fromCursor(cursor);
        cursor.close();

        return exerciseEntry;
    }

    // Query the entire table, return all rows
    public ArrayList<ExerciseEntry> fetchEntries() {
        Cursor cursor = getReadableDatabase().query(
                ExerciseEntryTableSchema.TABLE_NAME,
                ExerciseEntryTableSchema.PROJECTION,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<ExerciseEntry> exerciseEntries = new ArrayList<>();
        while (cursor.moveToNext()) {
            exerciseEntries.add(ExerciseEntry.fromCursor(cursor));
        }
        cursor.close();

        return exerciseEntries;
    }
}
