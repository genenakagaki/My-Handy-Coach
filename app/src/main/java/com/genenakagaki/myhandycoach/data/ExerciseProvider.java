package com.genenakagaki.myhandycoach.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.genenakagaki.myhandycoach.data.ExerciseContract.CONTENT_AUTHORITY;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.PATH_REACTION_EXERCISE;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.PATH_REACTION_EXERCISE_ID;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.PATH_REGULAR_EXERCISE;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.PATH_REGULAR_EXERCISE_ID;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.ReactionExerciseEntry;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.RegularExerciseEntry;

/**
 * Created by gene on 4/15/17.
 */

public class ExerciseProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int REGULAR_EXERCISE = 100;
    static final int REGULAR_EXERCISE_ID = 101;
    static final int REACTION_EXERCISE = 200;
    static final int REACTION_EXERCISE_ID = 201;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_REGULAR_EXERCISE, REGULAR_EXERCISE);
        matcher.addURI(authority, PATH_REGULAR_EXERCISE_ID, REGULAR_EXERCISE_ID);
        matcher.addURI(authority, PATH_REACTION_EXERCISE, REACTION_EXERCISE);
        matcher.addURI(authority, PATH_REACTION_EXERCISE_ID, REACTION_EXERCISE_ID);

        return matcher;
    }

    private ExerciseDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new ExerciseDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case REGULAR_EXERCISE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        RegularExerciseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REGULAR_EXERCISE_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        RegularExerciseEntry.TABLE_NAME,
                        projection,
                        RegularExerciseEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            case REACTION_EXERCISE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ReactionExerciseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REACTION_EXERCISE_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ReactionExerciseEntry.TABLE_NAME,
                        projection,
                        ReactionExerciseEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case REGULAR_EXERCISE:
                return RegularExerciseEntry.CONTENT_TYPE;
            case REACTION_EXERCISE:
                return ReactionExerciseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (sUriMatcher.match(uri)) {
            case REGULAR_EXERCISE:
                id = db.insert(RegularExerciseEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = RegularExerciseEntry.buildRegularUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case REACTION_EXERCISE:
                id = db.insert(ReactionExerciseEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ReactionExerciseEntry.buildReactionUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        // a null deletes all rows
        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case REGULAR_EXERCISE:
                rowsDeleted = db.delete(
                        RegularExerciseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REGULAR_EXERCISE_ID:
                rowsDeleted = db.delete(
                        RegularExerciseEntry.TABLE_NAME,
                        RegularExerciseEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)});
                break;
            case REACTION_EXERCISE:
                rowsDeleted = db.delete(
                        ReactionExerciseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REACTION_EXERCISE_ID:
                rowsDeleted = db.delete(
                        ReactionExerciseEntry.TABLE_NAME,
                        ReactionExerciseEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case REGULAR_EXERCISE:
                rowsUpdated = db.update(
                        RegularExerciseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REGULAR_EXERCISE_ID:
                rowsUpdated = db.update(
                        RegularExerciseEntry.TABLE_NAME,
                        values,
                        RegularExerciseEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)});
                break;
            case REACTION_EXERCISE:
                rowsUpdated = db.update(
                        ReactionExerciseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REACTION_EXERCISE_ID:
                rowsUpdated = db.update(
                        ReactionExerciseEntry.TABLE_NAME,
                        values,
                        ReactionExerciseEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return  rowsUpdated;
    }
}
