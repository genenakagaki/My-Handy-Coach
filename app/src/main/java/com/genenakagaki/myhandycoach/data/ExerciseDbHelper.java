package com.genenakagaki.myhandycoach.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.genenakagaki.myhandycoach.data.ExerciseContract.ReactionExerciseEntry;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.RegularExerciseEntry;

/**
 * Created by gene on 4/15/17.
 */

public class ExerciseDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "myhandycoach.db";

    public ExerciseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REGULAR_TABLE = "CREATE TABLE " + RegularExerciseEntry.TABLE_NAME +
                " (" +
                RegularExerciseEntry._ID + " INTEGER PRIMARY KEY, " +
                RegularExerciseEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RegularExerciseEntry.COLUMN_REPS + " INTEGER, " +
                RegularExerciseEntry.COLUMN_SETS + " INTEGER, " +
                RegularExerciseEntry.COLUMN_SET_DURATION + " INTEGER, " +
                RegularExerciseEntry.COLUMN_REST_DURATION + " INTEGER " +
                " );";

        final String SQL_CREATE_REACTION_TABLE = "CREATE TABLE " + ReactionExerciseEntry.TABLE_NAME +
                " (" +
                ReactionExerciseEntry._ID + " INTEGER PRIMARY KEY, " +
                ReactionExerciseEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ReactionExerciseEntry.COLUMN_REPS + " INTEGER, " +
                ReactionExerciseEntry.COLUMN_SETS + " INTEGER, " +
                ReactionExerciseEntry.COLUMN_CHOICES + " INTEGER, " +
                ReactionExerciseEntry.COLUMN_CHOICE_INTERVAL + " INTEGER, " +
                ReactionExerciseEntry.COLUMN_REST_DURATION + " INTEGER " +
                " );";

        db.execSQL(SQL_CREATE_REGULAR_TABLE);
        db.execSQL(SQL_CREATE_REACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RegularExerciseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReactionExerciseEntry.TABLE_NAME);
        onCreate(db);
    }
}
