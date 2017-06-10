package com.genenakagaki.myhandycoach.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.genenakagaki.myhandycoach.exception.PreferenceNotFoundException;

/**
 * Created by gene on 4/7/17.
 */

public class AppPreference {

    public static final String PREF_CURRENT_REGULAR_EXERCISE_ID = "current_regular_exercise_id";
    public static final String PREF_CURRENT_REACTION_EXERCISE_ID = "current_reaction_exercise_id";

    public static void setCurrentRegularExerciseId(Context context, long exerciseId) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(PREF_CURRENT_REGULAR_EXERCISE_ID, exerciseId);
        editor.commit();
    }

    public static void setCurrentReactionExerciseId(Context context, long exerciseId) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(PREF_CURRENT_REACTION_EXERCISE_ID, exerciseId);
        editor.commit();
    }

    public static long getCurrentRegularExerciseId(Context context) throws PreferenceNotFoundException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long id = pref.getLong(PREF_CURRENT_REGULAR_EXERCISE_ID, -1);

        if (id == -1) {
            throw new PreferenceNotFoundException("Preference not found: " + PREF_CURRENT_REGULAR_EXERCISE_ID);
        } else {
            return id;
        }
    }

    public static long getCurrentReactionExerciseId(Context context) throws PreferenceNotFoundException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long id = pref.getLong(PREF_CURRENT_REACTION_EXERCISE_ID, -1);

        if (id == -1) {
            throw new PreferenceNotFoundException("Preference not found: " + PREF_CURRENT_REACTION_EXERCISE_ID);
        } else {
            return id;
        }
    }

    public static void deleteCurrentRegularExercise(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove(PREF_CURRENT_REGULAR_EXERCISE_ID);
        editor.commit();
    }

    public static void deleteCurrentReactionExercise(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove(PREF_CURRENT_REACTION_EXERCISE_ID);
        editor.commit();
    }

}
