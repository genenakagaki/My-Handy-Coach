package com.genenakagaki.myhandycoach.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.model.ReactionExercise;
import com.genenakagaki.myhandycoach.exception.ExerciseAlreadyExistsException;
import com.genenakagaki.myhandycoach.exception.ExerciseNotFoundException;
import com.genenakagaki.myhandycoach.exception.PreferenceNotFoundException;

import timber.log.Timber;

import static com.genenakagaki.myhandycoach.data.AppPreference.getCurrentReactionExerciseId;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.ReactionExerciseEntry;

/**
 * Created by gene on 4/18/17.
 */

public class ReactionExerciseDb {

    public static ReactionExercise createExercise(Cursor c) {
        return new ReactionExercise(
                c.getLong(ReactionExerciseEntry.INDEX_ID),
                c.getString(ReactionExerciseEntry.INDEX_NAME),
                c.getInt(ReactionExerciseEntry.INDEX_REPS),
                c.getInt(ReactionExerciseEntry.INDEX_SETS),
                c.getInt(ReactionExerciseEntry.INDEX_CHOICES),
                c.getInt(ReactionExerciseEntry.INDEX_CHOICES_INTERVAL),
                c.getInt(ReactionExerciseEntry.INDEX_REST_DURATION));
    }

    public static ReactionExercise getExercise(Context context) throws ExerciseNotFoundException {
        Timber.d("getExercise");

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    ReactionExerciseEntry.CONTENT_URI,
                    ReactionExerciseEntry.COLUMNS,
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

    public static ReactionExercise getExercise(Context context, long exerciseId) throws ExerciseNotFoundException {
        return getExercise(context, ReactionExerciseEntry.buildReactionUri(exerciseId));
    }

    public static ReactionExercise getExercise(Context context, Uri uri) throws ExerciseNotFoundException {
        Timber.d("getExercise");
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    uri, ReactionExerciseEntry.COLUMNS, null, null, null);

            if (c.moveToFirst()) {
                return createExercise(c);
            } else {
                throw new ExerciseNotFoundException("ReactionExercise with uri '" + uri + "' not found.");
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public static ReactionExercise getExerciseByName(Context context, String name) throws ExerciseNotFoundException {
        Timber.d("getExerciseByName");

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    ReactionExerciseEntry.CONTENT_URI,
                    ReactionExerciseEntry.COLUMNS,
                    ReactionExerciseEntry.COLUMN_NAME + " = ?",
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

    public static ReactionExercise getCurrentExercise(Context context) {
        Timber.d("getCurrentExercise");

        long exerciseId;
        try {
            exerciseId = getCurrentReactionExerciseId(context);
        } catch (PreferenceNotFoundException pe) {
            try {
                Timber.d("Current regular exercise not set in preference");
                exerciseId = getExercise(context).id;
            } catch (ExerciseNotFoundException ee) {
                Timber.d("No regular exercise found.");
                exerciseId = insertDefaultExercise(context).id;
                AppPreference.setCurrentReactionExerciseId(context, exerciseId);
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

    public static void setCurrentExercise(Context context, long ReactionExerciseId) {
        Timber.d("setCurrentExercise");

        AppPreference.setCurrentReactionExerciseId(context, ReactionExerciseId);
    }

    public static ReactionExercise insertDefaultExercise(Context context) {
        Timber.d("insertDefaultExercise");
        ContentValues values = new ContentValues();
        values.put(ReactionExerciseEntry.COLUMN_NAME, context.getString(R.string.default_exercise_name));
        values.put(ReactionExerciseEntry.COLUMN_REPS, 0);
        values.put(ReactionExerciseEntry.COLUMN_SETS, 0);
        values.put(ReactionExerciseEntry.COLUMN_CHOICES, 0);
        values.put(ReactionExerciseEntry.COLUMN_CHOICE_INTERVAL, 0);
        values.put(ReactionExerciseEntry.COLUMN_REST_DURATION, 0);

        Uri uri = context.getContentResolver().insert(ReactionExerciseEntry.CONTENT_URI, values);

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

        ReactionExercise exercise = new ReactionExercise(0, name, 0, 0, 0, 0, 0);

        // Check for duplicate name
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    ReactionExerciseEntry.CONTENT_URI,
                    ReactionExerciseEntry.COLUMNS,
                    ReactionExerciseEntry.COLUMN_NAME + " = ?",
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
        values.put(ReactionExerciseEntry.COLUMN_NAME, exercise.name);
        values.put(ReactionExerciseEntry.COLUMN_REPS, exercise.reps);
        values.put(ReactionExerciseEntry.COLUMN_SETS, exercise.sets);
        values.put(ReactionExerciseEntry.COLUMN_CHOICES, exercise.choices);
        values.put(ReactionExerciseEntry.COLUMN_CHOICE_INTERVAL, exercise.choiceInterval);
        values.put(ReactionExerciseEntry.COLUMN_REST_DURATION, exercise.restDuration);

        return context.getContentResolver().insert(ReactionExerciseEntry.CONTENT_URI, values);
    }

    public static void updateExercise(Context context, ReactionExercise exercise) {
        ContentValues values = new ContentValues();
        values.put(ReactionExerciseEntry.COLUMN_REPS, exercise.reps);
        values.put(ReactionExerciseEntry.COLUMN_SETS, exercise.sets);
        values.put(ReactionExerciseEntry.COLUMN_CHOICES, exercise.choices);
        values.put(ReactionExerciseEntry.COLUMN_CHOICE_INTERVAL, exercise.choiceInterval);
        values.put(ReactionExerciseEntry.COLUMN_REST_DURATION, exercise.restDuration);

        context.getContentResolver().update(
                ReactionExerciseEntry.buildReactionUri(exercise.id), values, null, null);
    }

    public static void updateExerciseName(Context context, long exerciseId, String name) {
        ContentValues values = new ContentValues();
        values.put(ReactionExerciseEntry.COLUMN_NAME, name);

        context.getContentResolver().update(
                ReactionExerciseEntry.buildReactionUri(exerciseId), values, null, null);
    }

    public static void deleteExercise(Context context, long id) {
        Timber.d("deleteExercise");

        try {
            long currentId = AppPreference.getCurrentReactionExerciseId(context);

            if (currentId == id) {
                AppPreference.deleteCurrentReactionExercise(context);
            }
        } catch (PreferenceNotFoundException e) {
            Timber.d(e.getMessage());
            e.printStackTrace();
        }

        context.getContentResolver().delete(
                ReactionExerciseEntry.CONTENT_URI,
                ReactionExerciseEntry._ID + " = ?",
                new String[] {Long.toString(id)});
    }
}