package com.benavides.ramon.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Actor;
import com.benavides.ramon.popularmovies.data.CastActor;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.data.Review;
import com.benavides.ramon.popularmovies.data.Trailer;
import com.benavides.ramon.popularmovies.database.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class help to parse and covert data types about data bean classes
 */
public class DataHelper {


    public static ContentValues[] retrieveMoviesData(Context context, String movieCategory, int page) {

        HttpURLConnection urlConnection = null;
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            //Composing url to request data
            URL url = new URL(context.getString(R.string.tmdb_api_url) + movieCategory + context.getString(R.string.tmdb_api_key) + "&page=" + page);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Getting string from input stream
            String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

            //Parse Data
            movies = DataHelper.parseMoviesJson(context, jsonResult, page);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return DataHelper.prepareToInsertMovies(movies);

    }

    /**
     * Method to parse the movie data base json response
     *
     * @param json String with json content
     * @return
     */
    public static ArrayList<Movie> parseMoviesJson(Context context, String json, int page) throws JSONException {

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
            movie.setOrder((page - 1) * 20 + (i + 1));
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
            if (trailerObject.getString("site").equalsIgnoreCase("youtube")) {
                trailer.setId(trailerObject.getString("id"));
                trailer.setName(trailerObject.getString("name"));
                trailer.setSource(trailerObject.getString("key"));
                trailer.setMovieID(movieId);
                result.add(trailer);

            }
        }
        return result;
    }

    public static ArrayList<CastActor> parseCastJson(Context context, int movieId, String json) throws JSONException {

        ArrayList<CastActor> result = new ArrayList<>();

        JSONObject castData = new JSONObject(json);
        JSONArray cast = castData.getJSONArray("cast");
        for (int i = 0; i < cast.length(); i++) {
            JSONObject castObject = cast.getJSONObject(i);
            CastActor actor = new CastActor();
            actor.setId(castObject.getInt("id"));
            actor.setName(castObject.getString("name"));
            actor.setCharacter(castObject.getString("character"));
            actor.setPicture(context.getString(R.string.tmdb_poster_base_url) + "w185" + castObject.getString("profile_path"));
            actor.setMovieId(movieId);
            result.add(actor);

        }
        return result;
    }

    public static Actor parseActorJson(Context context, String json) throws JSONException {
        JSONObject actorData = new JSONObject(json);
        Actor actor = new Actor();
        actor.setId(actorData.getInt("id"));
        actor.setName(actorData.getString("name"));
        actor.setBiography(actorData.getString("biography"));
        actor.setBirthday(actorData.getString("birthday"));
        actor.setDeathday(actorData.getString("deathday"));
        actor.setPlace(actorData.getString("place_of_birth"));
        actor.setPopularity(actorData.getDouble("popularity"));
        actor.setPicture(context.getString(R.string.tmdb_poster_base_url) + "w500" + actorData.getString("profile_path"));
        return actor;
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
            values.put(MoviesContract.MovieEntry.COLUMN_ORDER, movie.getOrder());
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

    public static ContentValues[] prepareToInsertCast(ArrayList<CastActor> castActors) {
        ContentValues[] contentValues = new ContentValues[castActors.size()];
        for (int i = 0; i < castActors.size(); i++) {

            CastActor castActor = castActors.get(i);

            ContentValues values = new ContentValues();
            values.put(MoviesContract.CastEntry._ID, castActor.getId());
            values.put(MoviesContract.CastEntry.COLUMN_NAME, castActor.getName());
            values.put(MoviesContract.CastEntry.COLUMN_CHARACTER, castActor.getCharacter());
            values.put(MoviesContract.CastEntry.COLUMN_PICTURE, castActor.getPicture());
            values.put(MoviesContract.CastEntry.COLUMN_MOVIE_ID, castActor.getMovieId());

            contentValues[i] = values;
        }
        return contentValues;
    }

    public static ContentValues prepareToInsertActor(Actor actor) {
        if (actor == null)
            return null;

        ContentValues values = new ContentValues();
        values.put(MoviesContract.ActorsEntry._ID, actor.getId());
        values.put(MoviesContract.ActorsEntry.COLUMN_NAME, actor.getName());
        values.put(MoviesContract.ActorsEntry.COLUMN_BIOGRAPHY, actor.getBiography());
        values.put(MoviesContract.ActorsEntry.COLUMN_BIRTHDAY, actor.getBirthday());
        values.put(MoviesContract.ActorsEntry.COLUMN_DEATHDAY, actor.getDeathday());
        values.put(MoviesContract.ActorsEntry.COLUMN_PLACE, actor.getPlace());
        values.put(MoviesContract.ActorsEntry.COLUMN_POPULARITY, actor.getPopularity());
        values.put(MoviesContract.ActorsEntry.COLUMN_PICTURE, actor.getPicture());
        return values;
    }


    public static ArrayList<Review> getReviewsFromCursor(Cursor cursor) {
        ArrayList<Review> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Review review = new Review();
                review.setId(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_ID));
                review.setAuthor(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_AUTHOR));
                review.setContent(cursor.getString(MoviesContract.ReviewEntry.REVIEWS_COLUMN_CONTENT));
                review.setMovieId(cursor.getInt(MoviesContract.ReviewEntry.REVIEWS_COLUMN_MOVIE_ID));
                result.add(review);
            } while (cursor.moveToNext());
        }
        return result;
    }

    public static ArrayList<Trailer> getTrailersFromCursor(Cursor cursor) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Trailer trailer = new Trailer();
                trailer.setId(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_ID));
                trailer.setName(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_NAME));
                trailer.setSource(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_SOURCE));
                trailer.setMovieID(cursor.getInt(MoviesContract.TrailerEntry.TRAILER_COLUMN_MOVIE_ID));
                result.add(trailer);
            } while (cursor.moveToNext());
        }
        return result;
    }


}
