package com.benavides.ramon.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Review;
import com.benavides.ramon.popularmovies.data.Trailer;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DataTaskListener;
import com.benavides.ramon.popularmovies.utils.DataHelper;
import com.benavides.ramon.popularmovies.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Works checking cached data into database first
 */
public class ObtainDataTask extends AsyncTask<Integer, Void, Void> {

    private Context mContext;
    private DataTaskListener mCallabck;
    private ArrayList<Review> mReviews;
    private ArrayList<Trailer> mTrailers;

    public ObtainDataTask(Context context, DataTaskListener callback) {
        this.mContext = context;
        this.mCallabck = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mReviews = new ArrayList<>();
        mTrailers = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Integer... params) {

        /**
         * 1st- Check into database if I have movie's reviews and trailers.
         * yes- return from database
         * no- retrieve from API and store into database
         */

    // requesting database for reviews
        Cursor reviewCursor = mContext.getContentResolver().query(MoviesContract.ReviewEntry.buildReviewData(), MoviesContract.ReviewEntry.REVIEW_PROJECTION,
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " =?", new String[]{Integer.toString(params[0])}, null);

        if (reviewCursor != null && reviewCursor.getCount() > 0) {//there are reviews for this movie
            mReviews = getReviewsFromCursor(reviewCursor);
        } else {//no reviews found, so request API and store it into database
            ContentValues[] reviews = retrieveReviews(params[0]);
            mContext.getContentResolver().bulkInsert(MoviesContract.ReviewEntry.buildReviewData(), reviews);
        }

    // requesting database for trailers
        Cursor trailerCursor = mContext.getContentResolver().query(MoviesContract.TrailerEntry.buildTrailerData(), MoviesContract.TrailerEntry.TRAILER_PROJECTION,
                MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " =?", new String[]{Integer.toString(params[0])}, null);

        if (trailerCursor != null && trailerCursor.moveToFirst()) {//there are trailers for this movie
            mTrailers = getTrailersFromCursor(trailerCursor);
        } else { // no trailers, so request API and store it into database
            ContentValues[] trailers = retrieveTrailers(params[0]);
            mContext.getContentResolver().bulkInsert(MoviesContract.TrailerEntry.buildTrailerData(), trailers);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void value) {
        super.onPostExecute(value);

        if(mCallabck!= null)
            mCallabck.onApiTaskDone(mReviews, mTrailers);
    }

    private ArrayList<Review> getReviewsFromCursor(Cursor cursor) {
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

    private ArrayList<Trailer> getTrailersFromCursor(Cursor cursor) {
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


    private ContentValues[] retrieveReviews(int movieId) {
        HttpURLConnection urlConnection = null;

        try {
            //Composing url to request data
            URL url = new URL(mContext.getString(R.string.tmdb_api_url) + movieId + "/reviews" + mContext.getString(R.string.tmdb_api_key));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Getting string from input stream
            String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

            //Parse Data
            mReviews = DataHelper.parseReviewsJson(movieId, jsonResult);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return DataHelper.prepareToInsertReviews(mReviews);
    }


    private ContentValues[] retrieveTrailers(int movieId) {

        HttpURLConnection urlConnection = null;

        try {
            //Composing url to request data
            URL url = new URL(mContext.getString(R.string.tmdb_api_url) + movieId + "/videos" + mContext.getString(R.string.tmdb_api_key));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Getting string from input stream
            String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

            //Parse Data
            mTrailers = DataHelper.parseTrailersJson(movieId, jsonResult);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return DataHelper.prepareToInsertTrailers(mTrailers);
    }
}
