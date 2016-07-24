package com.benavides.ramon.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.benavides.ramon.popularmovies.database.MoviesContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by ramon on 21/7/16.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createMovieTestValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, " MovieTest");
        testValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, "This is a beautifull movie. The story occurs in a little town at Barcelona suburbs. A man, who wants to change his destiny, is studying a Udacity Android nanodegree...");
        testValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, "https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAfaAAAAJDMyNzViYTQxLWM2MTEtNDg1Yi1hY2QzLTVhMjFjMjA4MDlhOA.jpg");
        testValues.put(MoviesContract.MovieEntry.COLUMN_RATING, 10);
        testValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "07-21-2016");
        testValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, "https://media.licdn.com/mpr/mpr/shrinknp_400_400/AAEAAQAAAAAAAAfaAAAAJDMyNzViYTQxLWM2MTEtNDg1Yi1hY2QzLTVhMjFjMjA4MDlhOA.jpg");

        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columName = entry.getKey();
            int index = valueCursor.getColumnIndex(columName);
            assertFalse("Column '" + columName + "' not found " + error, index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value " +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(index));
        }

    }
}
