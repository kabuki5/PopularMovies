package com.benavides.ramon.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.utils.Utils;


/**
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieDBHelper mDbHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_CATEGORY = 101;
    static final int CATEGORIES = 200;
    static final int MOVIE_CATEGORY = 300;
    static final int REVIEWS = 400;
    static final int TRAILERS = 500;
    static final int CAST = 600;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_CATEGORIES, CATEGORIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIES_WITH_CATEGORY);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIE_CATEGORY, MOVIE_CATEGORY);
        uriMatcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(authority, MoviesContract.PATH_CAST, CAST);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDBHelper(getContext());

        // inserting categories
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
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int matchUri = mUriMatcher.match(uri);
        switch (matchUri) {
            case MOVIES:
                cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case MOVIE_CATEGORY:
                cursor = db.query(MoviesContract.MovieCategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case MOVIES_WITH_CATEGORY:
                int category = Utils.getCategoryByName(getContext(), MoviesContract.MovieEntry.getCategoryFromUri(uri));

//              subquery to obtain just the movies from the category selected
                String selectQuery = "SELECT * FROM " + MoviesContract.MovieEntry.TABLE_NAME + " WHERE " + MoviesContract.MovieEntry._ID +
                        " IN ( SELECT " + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " FROM " + MoviesContract.MovieCategoryEntry.TABLE_NAME
                        + " WHERE " + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " = " + category + " );";

                cursor = db.rawQuery(selectQuery, null);
                break;

            case CATEGORIES:
                cursor = db.query(MoviesContract.CategoryEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;

            case REVIEWS:
                cursor = db.query(MoviesContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case TRAILERS:
                cursor = db.query(MoviesContract.TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case CAST:
                cursor = db.query(MoviesContract.CastEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
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


    //Insert method manages favorites inserts and deletes
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);

        Uri resultUri = null;
        long result;

        switch (match) {
            case MOVIES:

                int movieID = values.getAsInteger(MoviesContract.MovieEntry._ID);

                if (existsMovie(movieID, db)) {// If movie exists then it is deleted
                    String selection = MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " = " + Integer.toString(movieID) +
                            " AND " + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " = " + Utils.getCategoryByName(getContext(), getContext().getString(R.string.favorites_low));
                    delete(MoviesContract.MovieEntry.buildMoviesData(), selection, null);
                } else {
                    //                    Inserting movie as favorite.
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID, Utils.getCategoryByName(getContext(), getContext().getString(R.string.favorites_low)));
                    contentValues.put(MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID, movieID);

                    result = db.insert(MoviesContract.MovieCategoryEntry.TABLE_NAME, null, contentValues);
                    resultUri = MoviesContract.MovieEntry.buildMoviesUri(result);
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
        int rowsDeleted = 0;


        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.MovieCategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_CATEGORY:
                try {
                    // delete movies
                    final String deleteSentence = "DELETE FROM " + MoviesContract.MovieEntry.TABLE_NAME +
                            " WHERE " + MoviesContract.MovieEntry._ID + " NOT IN " +
                            "(SELECT " + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " FROM " + MoviesContract.MovieCategoryEntry.TABLE_NAME + " WHERE " + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " <> ?);";

                    db.execSQL(deleteSentence, selectionArgs);

// delete relation if there is some film shared between categories
                    final String freeCategory = "DELETE FROM " + MoviesContract.MovieCategoryEntry.TABLE_NAME + " WHERE " +
                            MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " = " + selectionArgs[0];

                    db.execSQL(freeCategory);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /*  // delete relationship
                           final String deleteMovieCategoryRelation = "DELETE FROM " + MoviesContract.MovieCategoryEntry.TABLE_NAME +
                                   " WHERE " + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " = " + selectionArgs[0];

                           db.execSQL(deleteMovieCategoryRelation);
   */
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
//              getting category ID
                int categoryId = Utils.getCategoryByName(getContext(), MoviesContract.MovieEntry.getCategoryFromUri(uri));

//              delete movies from Movies table and from Movie-Category table
                delete(MoviesContract.MovieCategoryEntry.buildMovieCategoryData(), null, new String[]{Integer.toString(categoryId)});

                db.beginTransaction();
//              inserting movies into Movies table and Movie-Category relation
                try {
                    for (ContentValues value : values) {
                        db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);

                        //Inserting relation into Category-Movie Table
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID, categoryId);
                        contentValues.put(MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID, value.getAsInteger(MoviesContract.MovieEntry._ID));

                        long res = db.insert(MoviesContract.MovieCategoryEntry.TABLE_NAME, null, contentValues);
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

            case REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values) {
                        long res = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, contentValues);
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

            case TRAILERS:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values) {
                        long res = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, contentValues);
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

            case CAST:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values) {
                        long res = db.insert(MoviesContract.CastEntry.TABLE_NAME, null, contentValues);
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

            default:
                return super.bulkInsert(uri, values);
        }
    }


    //    Checks if movie exists
    private boolean existsMovie(int movieID, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + MoviesContract.MovieCategoryEntry.TABLE_NAME +
                " WHERE " + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " = " + Integer.toString(movieID) +
                " AND " + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " = " +
                Utils.getCategoryByName(getContext(), getContext().getString(R.string.favorites_low)), null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null)
            cursor.close();

        return exists;
    }

    private Integer[] getIds(Cursor cursor) {
        Integer[] ids = null;
        if (cursor != null && cursor.moveToFirst()) {
            ids = new Integer[cursor.getCount()];
            int i = 0;
            do {
                ids[i] = cursor.getInt(0);
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ids;
    }
}
