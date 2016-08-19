package com.benavides.ramon.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Category;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.utils.CursorUtils;


/**
 * Created by ramon on 21/7/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieDBHelper mDbHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_CATEGORY = 101;
    static final int CATEGORIES = 200;
    static final int MOVIE_CATEGORY = 300;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_CATEGORIES, CATEGORIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_CATEGORY);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIE_CATEGORY, MOVIE_CATEGORY);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDBHelper(getContext());

//        inserting categories
        ContentValues[] insertValues = new ContentValues[3];
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.CategoryEntry._ID, 0);
        contentValues.put(MoviesContract.CategoryEntry.COLUMN_NAME, getContext().getString(R.string.popular_low));
        insertValues[0] = contentValues;

        contentValues = new ContentValues();
        contentValues.put(MoviesContract.CategoryEntry._ID, 1);
        contentValues.put(MoviesContract.CategoryEntry.COLUMN_NAME, getContext().getString(R.string.top_rated_low));
        insertValues[1] = contentValues;

        contentValues = new ContentValues();
        contentValues.put(MoviesContract.CategoryEntry._ID, 2);
        contentValues.put(MoviesContract.CategoryEntry.COLUMN_NAME, getContext().getString(R.string.favorites_low));
        insertValues[2] = contentValues;

        bulkInsert(MoviesContract.CategoryEntry.buildCategoryData(), insertValues);

        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int matchUri = mUriMatcher.match(uri);
        switch (matchUri) {
            case MOVIES:

                int category = getCategoryByName(selectionArgs[0]);

//              subquery to obtain just the movies from the category selected
                String selectQuery = "SELECT * FROM " + MoviesContract.MovieEntry.TABLE_NAME + " WHERE " + MoviesContract.MovieEntry._ID +
                        " IN ( SELECT " + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " FROM " + MoviesContract.MovieCategoryEntry.TABLE_NAME
                        + " WHERE " + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " = " + category + " );";

                cursor = db.rawQuery(selectQuery, null);
                break;
            case CATEGORIES:
                cursor = db.query(MoviesContract.CategoryEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
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

        Uri resultUri = null;
        long result;

        switch (match) {
            case MOVIES:
//                    Inserting movie as favorite.
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID, getCategoryByName(getContext().getString(R.string.favorites_low)));
                contentValues.put(MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID, values.getAsInteger(MoviesContract.MovieEntry._ID));

                result = db.insert(MoviesContract.MovieCategoryEntry.TABLE_NAME, null, contentValues);
                resultUri = MoviesContract.MovieEntry.buildMoviesUri(result);

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

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnCount = 0;

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES_WITH_CATEGORY:
                db.beginTransaction();
                try {
                    int categoryId = Integer.parseInt(MoviesContract.MovieEntry.getCategoryFromUri(uri));

                    for (ContentValues contentValues : values) {
                        db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);

                        //Inserting relation into Category-Movie Table
                        ContentValues value = new ContentValues();
                        value.put(MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID, categoryId);
                        value.put(MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID, contentValues.getAsInteger(MoviesContract.MovieEntry._ID));

                        long res = db.insert(MoviesContract.MovieCategoryEntry.TABLE_NAME, null, value);
                        if (res != -1) {
                            returnCount++;
                        }
                    }


                    //TODO => insert category - movie for movies


                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CATEGORIES:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values) {
                        long res = db.insert(MoviesContract.CategoryEntry.TABLE_NAME, null, contentValues);
                        if (res != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOVIE_CATEGORY:


                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    public int getCategoryByName(String name) {
        //Obtaining category ID
        Cursor cursor = query(MoviesContract.CategoryEntry.buildCategoryData(),
                MoviesContract.CategoryEntry.CATEGORIES_PROJECTION, MoviesContract.CategoryEntry.COLUMN_NAME + "=? ", new String[]{name}, null);
        int category = CursorUtils.getCategory(cursor);

        return category;

    }
}
