package com.benavides.ramon.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.data.Review;
import com.benavides.ramon.popularmovies.data.Trailer;
import com.benavides.ramon.popularmovies.database.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class help to parse and covert data types about data bean classes
 */
public class DataHelper {


    /**
     * Method to parse the movie data base json response
     *
     * @param json String with json content
     * @return
     */
    public static ArrayList<Movie> parseMoviesJson(Context context, String json) throws JSONException {

        ArrayList<Movie> result = new ArrayList<>();

        JSONObject movieData = new JSONObject(json);
        JSONArray movies = movieData.getJSONArray("results");
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movieObject = movies.getJSONObject(i);
            Movie movie = new Movie();
            movie.setId(movieObject.getInt("id"));
            movie.setOriginalTitle(movieObject.getString("original_title"));
            movie.setPoster(context.getString(R.string.tmdb_poster_base_url) + "w185" + movieObject.getString("poster_path"));
            movie.setBackdrop(context.getString(R.string.tmdb_poster_base_url) + "w500" + movieObject.getString("backdrop_path"));
            movie.setRating(movieObject.getDouble("vote_average"));
            movie.setReleaseDate(movieObject.getString("release_date"));
            movie.setSynopsis(movieObject.getString("overview"));
            result.add(movie);
        }
        return result;
    }

    public static ArrayList<Review> parseReviewsJson(int movieId, String json) throws JSONException {

        ArrayList<Review> result = new ArrayList<>();

        JSONObject reviewsData = new JSONObject(json);
        JSONArray reviews = reviewsData.getJSONArray("results");
        for (int i = 0; i < reviews.length(); i++) {
            JSONObject reviewObject = reviews.getJSONObject(i);
            Review review = new Review();
            review.setId(reviewObject.getString("id"));
            review.setAuthor(reviewObject.getString("author"));
            review.setContent(reviewObject.getString("content"));
            review.setMovieId(movieId);
            result.add(review);

        }
        return result;
    }

    public static ArrayList<Trailer> parseTrailersJson(int movieId, String json) throws JSONException {

        ArrayList<Trailer> result = new ArrayList<>();

        JSONObject trailersData = new JSONObject(json);
        JSONArray trailers = trailersData.getJSONArray("results");
        for (int i = 0; i < trailers.length(); i++) {
            JSONObject trailerObject = trailers.getJSONObject(i);
            Trailer trailer = new Trailer();
            if(trailerObject.getString("site").equalsIgnoreCase("youtube")){
                trailer.setId(trailerObject.getString("id"));
                trailer.setName(trailerObject.getString("name"));
                trailer.setSource(trailerObject.getString("key"));
                trailer.setMovieID(movieId);
                result.add(trailer);

            }
        }
        return result;
    }

    public static Movie getMovieFromCursor(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getInt(MoviesContract.MovieEntry.MOVIES_COLUMN_ID));
        movie.setOriginalTitle(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE));
        movie.setSynopsis(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_SYNOPSIS));
        movie.setPoster(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_POSTER));
        movie.setRating(cursor.getDouble(MoviesContract.MovieEntry.MOVIES_COLUMN_RATING));
        movie.setReleaseDate(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_RELEASE_DATE));
        movie.setBackdrop(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_BACKDROP));
        return movie;
    }

    public static ContentValues[] prepareToInsertMovies(ArrayList<Movie> movies) {
        ContentValues[] contentValues = new ContentValues[movies.size()];
        for (int i = 0; i < movies.size(); i++) {

            Movie movie = movies.get(i);

            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieEntry._ID, movie.getId());
            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getOriginalTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getPoster());
            values.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getRating());
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdrop());

            contentValues[i] = values;
        }
        return contentValues;
    }


    public static ContentValues[] prepareToInsertReviews(ArrayList<Review> reviews) {
        ContentValues[] contentValues = new ContentValues[reviews.size()];
        for (int i = 0; i < reviews.size(); i++) {

            Review review = reviews.get(i);

            ContentValues values = new ContentValues();
            values.put(MoviesContract.ReviewEntry._ID, review.getId());
            values.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            values.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            values.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, review.getMovieId());

            contentValues[i] = values;
        }
        return contentValues;
    }

    public static ContentValues[] prepareToInsertTrailers(ArrayList<Trailer> trailers) {
        ContentValues[] contentValues = new ContentValues[trailers.size()];
        for (int i = 0; i < trailers.size(); i++) {

            Trailer trailer = trailers.get(i);

            ContentValues values = new ContentValues();
            values.put(MoviesContract.TrailerEntry._ID, trailer.getId());
            values.put(MoviesContract.TrailerEntry.COLUMN_NAME, trailer.getName());
            values.put(MoviesContract.TrailerEntry.COLUMN_SOURCE, trailer.getSource());
            values.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, trailer.getMovieID());

            contentValues[i] = values;
        }
        return contentValues;
    }

    public static ArrayList<Review> getReviewsFromCursor(Cursor cursor) {
        ArrayList<Review> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Review review = new Review();
                review.setId(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_ID));
                review.setAuthor(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_AUTHOR));
                review.setContent(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_CONTENT));
                review.setMovieId(cursor.getInt(MoviesContract.ReviewEntry.REVIEWS_COLUMN_MOVIE_ID));
                result.add(review);
            }while(cursor.moveToNext());
        }
        return result;
    }

    public static ArrayList<Trailer> getTrailersFromCursor(Cursor cursor) {
        ArrayList<Trailer> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Trailer trailer = new Trailer();
                trailer.setId(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_ID));
                trailer.setName(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_NAME));
                trailer.setSource(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_SOURCE));
                trailer.setMovieID(cursor.getInt(MoviesContract.TrailerEntry.TRAILER_COLUMN_MOVIE_ID));
                result.add(trailer);
            }while(cursor.moveToNext());
        }
        return result;
    }
}
