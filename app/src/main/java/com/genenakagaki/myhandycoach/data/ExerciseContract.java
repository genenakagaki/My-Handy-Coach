package com.genenakagaki.myhandycoach.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gene on 4/15/17.
 */

public class ExerciseContract {

    public static final String CONTENT_AUTHORITY = "com.genenakagaki.myhandycoach";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REGULAR_EXERCISE = "regular";
    public static final String PATH_REGULAR_EXERCISE_ID = PATH_REGULAR_EXERCISE + "/#";
    public static final String PATH_REACTION_EXERCISE = "reaction";
    public static final String PATH_REACTION_EXERCISE_ID = PATH_REACTION_EXERCISE + "/#";

    public static final class RegularExerciseEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REGULAR_EXERCISE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REGULAR_EXERCISE;

        public static final String TABLE_NAME = "regular";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_SETS = "sets";
        public static final String COLUMN_SET_DURATION = "set_duration";
        public static final String COLUMN_REST_DURATION = "rest_duration";

        public static final String[] COLUMNS = {
                TABLE_NAME + "." + _ID,
                COLUMN_NAME,
                COLUMN_REPS,
                COLUMN_SETS,
                COLUMN_SET_DURATION,
                COLUMN_REST_DURATION
        };
        public static final int INDEX_ID = 0;
        public static final int INDEX_NAME = 1;
        public static final int INDEX_REPS = 2;
        public static final int INDEX_SETS = 3;
        public static final int INDEX_SET_DURATION = 4;
        public static final int INDEX_REST_DURATION = 5;

        public static Uri buildRegularUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReactionExerciseEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REACTION_EXERCISE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REACTION_EXERCISE;

        public static final String TABLE_NAME = "reaction";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_SETS = "sets";
        public static final String COLUMN_CHOICES = "choices";
        public static final String COLUMN_CHOICE_INTERVAL = "choice_interval";
        public static final String COLUMN_REST_DURATION = "rest_duration";

        public static final String[] COLUMNS = {
                TABLE_NAME + "." + _ID,
                COLUMN_NAME,
                COLUMN_REPS,
                COLUMN_SETS,
                COLUMN_CHOICES,
                COLUMN_CHOICE_INTERVAL,
                COLUMN_REST_DURATION
        };
        public static final int INDEX_ID = 0;
        public static final int INDEX_NAME = 1;
        public static final int INDEX_REPS = 2;
        public static final int INDEX_SETS = 3;
        public static final int INDEX_CHOICES = 4;
        public static final int INDEX_CHOICES_INTERVAL = 5;
        public static final int INDEX_REST_DURATION = 6;

        public static Uri buildReactionUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
