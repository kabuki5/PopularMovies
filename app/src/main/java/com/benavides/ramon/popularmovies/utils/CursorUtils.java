package com.benavides.ramon.popularmovies.utils;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;

import java.util.ArrayList;

/**
 * Created by ramon on 24/7/16.
 */
public class CursorUtils {

    public static Cursor convertMovieArrayListToCursor(ArrayList<Movie> movies) {
        MatrixCursor cursor = new MatrixCursor(MoviesContract.MovieEntry.MOVIES_PROJECTION);
        int id = 0;
        for (Movie movie : movies) {
            cursor.addRow(new Object[]{id, movie.getOriginalTitle(), movie.getSynopsis(), movie.getPoster(), movie.getRating(), movie.getReleaseDate(), movie.getBackdrop()});
            id++;
        }
        return cursor;
    }


    public static ArrayList<Movie> convertMovieCursorToArrayList(Cursor cursor) {
        ArrayList<Movie> movies = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                Movie movie = new Movie();
                movie.setOriginalTitle(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE));
                movie.setSynopsis(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_SYNOPSIS));
                movie.setPoster(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_POSTER));
                movie.setReleaseDate(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_RELEASE_DATE));
                movie.setBackdrop(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_BACKDROP));
                movie.setRating(cursor.getLong(MoviesContract.MovieEntry.MOVIES_COLUMN_RATING));
                movies.add(movie);
            }while (cursor.moveToNext());
        }

        return movies;
    }

    public static Movie getMovieFromCursor(Cursor cursor){
        Movie movie = new Movie();
        movie.setOriginalTitle(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE));
        movie.setSynopsis(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_SYNOPSIS));
        movie.setPoster(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_POSTER));
        movie.setRating(cursor.getDouble(MoviesContract.MovieEntry.MOVIES_COLUMN_RATING));
        movie.setReleaseDate(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_RELEASE_DATE));
        movie.setBackdrop(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_BACKDROP));
        return movie;
    }

}
