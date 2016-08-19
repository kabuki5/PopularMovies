package com.benavides.ramon.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ramon on 21/7/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movies.db";



    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                MoviesContract.MovieEntry.COLUMN_BACKDROP + " TEXT NOT NULL);";

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + MoviesContract.CategoryEntry.TABLE_NAME + " (" +
                MoviesContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.CategoryEntry.COLUMN_NAME + " TEXT NOT NULL);";

        final String SQL_CREATE_MOVIES_CATEGORIES_TABLE = "CREATE TABLE " + MoviesContract.MovieCategoryEntry.TABLE_NAME + " (" +
                MoviesContract.MovieCategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                "UNIQUE ("+  MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID+" , "+  MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID +" ));";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_CATEGORIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//      dropping older table
      //  db.execSQL("DROP TABLE IF EXISTS "+ MoviesContract.MovieEntry.TABLE_NAME);

//      create tables
        //onCreate(db);


        //Really I don't want drop table and lose the user data. In new versions, this method should contains "ALTER TABLE" clauses.

    }
}
