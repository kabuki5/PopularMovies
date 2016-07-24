package com.benavides.ramon.popularmovies.data;

import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.benavides.ramon.popularmovies.database.MoviesContract;

/**
 * Created by ramon on 21/7/16.
 */
public class TestProvider extends AndroidTestCase {

    public void testInsert() {
        Uri result = getContext().getContentResolver()
                .insert(MoviesContract.MovieEntry.buildMoviesData(), TestUtilities.createMovieTestValues());

        assertTrue(result != null);
    }

    public void testQuery() {
        Cursor cursor = getContext().getContentResolver().query(MoviesContract.MovieEntry.buildMoviesData(), null, null, null, null);

        assertTrue("Cursor should not be null", cursor != null);

        assertTrue("Error: No Records returned from provider movie query", cursor.moveToFirst());
    }

    public void testDelete() {
        getContext().getContentResolver()
                .delete(MoviesContract.MovieEntry.buildMoviesData(), null, null);

        Cursor cursor = getContext().getContentResolver().query(MoviesContract.MovieEntry.buildMoviesData(), null, null, null, null);

        assertTrue("Cursor should not be null", cursor != null);

        assertEquals("Error: Rows not deleted",0, cursor.getCount());

    }


}
