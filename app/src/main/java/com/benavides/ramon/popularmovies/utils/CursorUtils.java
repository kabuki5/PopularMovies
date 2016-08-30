package com.benavides.ramon.popularmovies.utils;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;

import java.util.ArrayList;

/**
 */
public class CursorUtils {

    public static Cursor convertMovieArrayListToCursor(ArrayList<Movie> movies) {
        MatrixCursor cursor = new MatrixCursor(MoviesContract.MovieEntry.MOVIES_PROJECTION);
        for (Movie movie : movies) {
            cursor.addRow(new Object[]{movie.getId(), movie.getOriginalTitle(), movie.getSynopsis(), movie.getPoster(), movie.getRating(), movie.getReleaseDate(), movie.getBackdrop()});
        }
        return cursor;
    }


    public static ArrayList<Movie> convertMovieCursorToArrayList(Cursor cursor) {
        ArrayList<Movie> movies = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setOriginalTitle(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE));
                movie.setSynopsis(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_SYNOPSIS));
                movie.setPoster(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_POSTER));
                movie.setReleaseDate(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_RELEASE_DATE));
                movie.setBackdrop(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_BACKDROP));
                movie.setRating(cursor.getLong(MoviesContract.MovieEntry.MOVIES_COLUMN_RATING));
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        return movies;
    }



    public static ContentValues[] prepareToInsertMovieCategories(ArrayList<Movie> movies, int category) {
        ContentValues[] contentValues = new ContentValues[movies.size()];
        for (int i = 0; i < movies.size(); i++) {

            Movie movie = movies.get(i);

            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID, movie.getId());
            values.put(MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID, category);

            contentValues[i] = values;
        }

        return contentValues;
    }


}
