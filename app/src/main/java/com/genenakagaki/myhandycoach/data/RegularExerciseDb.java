package com.genenakagaki.myhandycoach.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;
import com.genenakagaki.myhandycoach.exception.ExerciseAlreadyExistsException;
import com.genenakagaki.myhandycoach.exception.ExerciseNotFoundException;
import com.genenakagaki.myhandycoach.exception.PreferenceNotFoundException;

import timber.log.Timber;

import static com.genenakagaki.myhandycoach.data.ExerciseContract.RegularExerciseEntry;

/**
 * Created by gene on 4/18/17.
 */

public class RegularExerciseDb {

    public static RegularExercise createExercise(Cursor c) {
        return new RegularExercise(
                c.getLong(RegularExerciseEntry.INDEX_ID),
                c.getString(RegularExerciseEntry.INDEX_NAME),
                c.getInt(RegularExerciseEntry.INDEX_REPS),
                c.getInt(RegularExerciseEntry.INDEX_SETS),
                c.getInt(RegularExerciseEntry.INDEX_SET_DURATION),
                c.getInt(RegularExerciseEntry.INDEX_REST_DURATION));
    }

    public static RegularExercise getExercise(Context context) throws ExerciseNotFoundException {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    RegularExerciseEntry.CONTENT_URI,
                    RegularExerciseEntry.COLUMNS,
                    null, null, null);

            if (c.moveToFirst()) {
                return createExercise(c);
            } else {
                throw new ExerciseNotFoundException("No regular exercise found.");
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public static RegularExercise getExercise(Context context, long exerciseId) throws ExerciseNotFoundException {
        return getExercise(context, RegularExerciseEntry.buildRegularUri(exerciseId));
    }

    public static RegularExercise getExercise(Context context, Uri uri) throws ExerciseNotFoundException {
        Timber.d("getExercise");
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    uri, RegularExerciseEntry.COLUMNS, null, null, null);

            if (c.moveToFirst()) {
                return createExercise(c);
            } else {
                throw new ExerciseNotFoundException("RegularExercise with uri '" + uri + "' not found.");
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public static RegularExercise getExerciseByName(Context context, String name) throws ExerciseNotFoundException {
        Timber.d("getExerciseByName");

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    RegularExerciseEntry.CONTENT_URI,
                    RegularExerciseEntry.COLUMNS,
                    RegularExerciseEntry.COLUMN_NAME + " = ?",
                    new String[] {name}, null);

            if (c.moveToFirst()) {
                return createExercise(c);
            } else {
                throw new ExerciseNotFoundException("Regular exercise with name '" + name + "' not found");
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public static RegularExercise getCurrentExercise(Context context) {
        Timber.d("getCurrentExercise");

        long exerciseId;
        try {
            exerciseId = AppPreference.getCurrentRegularExerciseId(context);
        } catch (PreferenceNotFoundException pe) {
            try {
                Timber.d("Current regular exercise not set in preference");
                exerciseId = getExercise(context).id;
            } catch (ExerciseNotFoundException ee) {
                Timber.d("No regular exercise found.");
                exerciseId = insertDefaultExercise(context).id;
                AppPreference.setCurrentRegularExerciseId(context, exerciseId);
            }
        }

        try {
            return getExercise(context, exerciseId);
        } catch (ExerciseNotFoundException e) {
            Timber.d(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void setCurrentExercise(Context context, long regularExerciseId) {
        Timber.d("setCurrentExercise");

        AppPreference.setCurrentRegularExerciseId(context, regularExerciseId);
    }

    public static RegularExercise insertDefaultExercise(Context context) {
        Timber.d("insertDefaultExercise");
        ContentValues values = new ContentValues();
        values.put(RegularExerciseEntry.COLUMN_NAME, context.getString(R.string.default_exercise_name));
        values.put(RegularExerciseEntry.COLUMN_REPS, 0);
        values.put(RegularExerciseEntry.COLUMN_SETS, 0);
        values.put(RegularExerciseEntry.COLUMN_SET_DURATION, 0);
        values.put(RegularExerciseEntry.COLUMN_REST_DURATION, 0);

        Uri uri = context.getContentResolver().insert(RegularExerciseEntry.CONTENT_URI, values);

        try {
            return getExercise(context, uri);
        } catch (ExerciseNotFoundException e) {
            Timber.d(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Uri insertExercise(Context context, String name) throws ExerciseAlreadyExistsException {
        Timber.d("insertExercise");

        // Check for duplicate name
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    RegularExerciseEntry.CONTENT_URI,
                    RegularExerciseEntry.COLUMNS,
                    RegularExerciseEntry.COLUMN_NAME + " = ?",
                    new String[] {name},
                    null);

            if (c.moveToFirst()) {
                throw new ExerciseAlreadyExistsException(
                        String.format("A regular exercise with the name '%s' already exists", name));
            }
        } finally {
            if (c != null) c.close();
        }

        ContentValues values = new ContentValues();
        values.put(RegularExerciseEntry.COLUMN_NAME, name);
        values.put(RegularExerciseEntry.COLUMN_REPS, 0);
        values.put(RegularExerciseEntry.COLUMN_SETS, 0);
        values.put(RegularExerciseEntry.COLUMN_SET_DURATION, 0);
        values.put(RegularExerciseEntry.COLUMN_REST_DURATION, 0);

        return context.getContentResolver().insert(RegularExerciseEntry.CONTENT_URI, values);
    }

    public static void updateExercise(Context context, RegularExercise exercise) {
        ContentValues values = new ContentValues();
        values.put(RegularExerciseEntry.COLUMN_REPS, exercise.reps);
        values.put(RegularExerciseEntry.COLUMN_SETS, exercise.sets);
        values.put(RegularExerciseEntry.COLUMN_SET_DURATION, exercise.setDuration);
        values.put(RegularExerciseEntry.COLUMN_REST_DURATION, exercise.restDuration);

        context.getContentResolver().update(
                RegularExerciseEntry.buildRegularUri(exercise.id), values, null, null);
    }

    public static void updateExerciseName(Context context, long exerciseId, String name) {
        ContentValues values = new ContentValues();
        values.put(RegularExerciseEntry.COLUMN_NAME, name);

        context.getContentResolver().update(
                RegularExerciseEntry.buildRegularUri(exerciseId), values, null, null);
    }

    public static void deleteExercise(Context context, long id) {
        Timber.d("deleteExercise");

        try {
            long currentId = AppPreference.getCurrentRegularExerciseId(context);

            if (currentId == id) {
                AppPreference.deleteCurrentRegularExercise(context);
            }
        } catch (PreferenceNotFoundException e) {
            Timber.d(e.getMessage());
            e.printStackTrace();
        }

        context.getContentResolver().delete(
                RegularExerciseEntry.CONTENT_URI,
                RegularExerciseEntry._ID + " = ?",
                new String[] {Long.toString(id)});
    }
}
