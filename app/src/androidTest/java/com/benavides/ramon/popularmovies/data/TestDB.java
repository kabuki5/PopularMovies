package com.benavides.ramon.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.benavides.ramon.popularmovies.database.MovieDBHelper;
import com.benavides.ramon.popularmovies.database.MoviesContract;

/**
 * Created by ramon on 21/7/16.
 */
public class TestDB extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
    }


    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void testDBCreateFunction() throws Throwable {
        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();

        assertTrue("DB should be open", db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: database has not been created correctly", c.moveToFirst());

        c.close();

        db.close();
    }

    public long testMoviesTable() {

//        getting db instance
        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();

//        getting test values
        ContentValues testValues = TestUtilities.createMovieTestValues();

//        inserting data into movies table
        long rowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);

//        checking if
        assertTrue("No rows has been found", rowId != -1);

//      testing query
        Cursor cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME,
                null, // all columns
                null, // columns for where
                null, // values for where
                null, // columns grup by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from Movie query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: data validation failed", cursor, testValues);

        assertFalse("Error: more than one record returned", cursor.moveToNext());

        cursor.close();

        db.close();

        return rowId;

    }


}
