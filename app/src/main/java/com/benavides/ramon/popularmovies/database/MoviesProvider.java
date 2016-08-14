package com.benavides.ramon.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


/**
 * Created by ramon on 21/7/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieDBHelper mDbHelper;

    static final int MOVIES = 100;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDBHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        int matchUri = mUriMatcher.match(uri);
        switch (matchUri) {
            case MOVIES:
                cursor = getAllMovies();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);

        Uri resultUri;
        switch (match) {
            case MOVIES:
                long result = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (result > 0) {
                    resultUri = MoviesContract.MovieEntry.buildMoviesUri(result);

                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = mUriMatcher.match(uri);
        int rowsDeleted;


        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


//    Database methods

    private Cursor getAllMovies() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);

        return cursor;
    }
}
