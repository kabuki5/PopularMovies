package com.benavides.ramon.popularmovies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.benavides.ramon.popularmovies.R;

/**
 * Database helper class. Defines tables creation and upgrades.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movies.db";

    private Context mContext;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    //    Creating the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_BACKDROP + " TEXT NOT NULL);";

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + MoviesContract.CategoryEntry.TABLE_NAME + " (" +
                MoviesContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.CategoryEntry.COLUMN_NAME + " TEXT NOT NULL);";

        final String SQL_CREATE_MOVIES_CATEGORIES_TABLE = "CREATE TABLE " + MoviesContract.MovieCategoryEntry.TABLE_NAME + " (" +
                MoviesContract.MovieCategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                "UNIQUE (" + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " , " + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " )," +
                "FOREIGN KEY (" + MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + ") REFERENCES " + MoviesContract.CategoryEntry.TABLE_NAME + " (" + MoviesContract.CategoryEntry._ID + ") ," +
                "FOREIGN KEY (" + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry._ID + "));";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "UNIQUE ( " + MoviesContract.ReviewEntry._ID + " , " + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " ), " +
                "FOREIGN KEY ( " + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " ) REFERENCES " + MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry._ID + "));";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.TrailerEntry.TABLE_NAME + " (" +
                MoviesContract.TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.TrailerEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "UNIQUE ( " + MoviesContract.TrailerEntry._ID + " , " + MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " ), " +
                "FOREIGN KEY ( " + MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " ) REFERENCES " + MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry._ID + "));";

        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_CATEGORIES_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
