package com.benavides.ramon.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Actor;
import com.benavides.ramon.popularmovies.data.CastActor;
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
public class ObtainDataTask extends AsyncTask<Integer, Void, Boolean> {

    private static final String TAG = ObtainDataTask.class.getSimpleName();

    private Context mContext;
    private DataTaskListener mCallback;

    public static final int TYPE_REVIEWS = 1;
    public static final int TYPE_TRAILERS = 2;
    public static final int TYPE_CAST = 3;
    public static final int TYPE_ACTOR = 4;
    public static final int TYPE_MOVIES = 5;

    private int mType;
    private String categoryName;

    public ObtainDataTask(Context context, DataTaskListener callback, int type) {
        this.mContext = context;
        this.mCallback = callback;
        this.mType = type;
    }

    public ObtainDataTask(Context context, DataTaskListener callback, int type, String category) {
        this.mContext = context;
        this.mCallback = callback;
        this.categoryName = category;
        this.mType = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Integer... params) {


        switch (mType) {
            case TYPE_REVIEWS:
                Log.d(TAG, "requesting API to get reviews");
                ContentValues[] reviews = retrieveReviews(params[0]);
                if (reviews != null && reviews.length > 0) {
                    mContext.getContentResolver().bulkInsert(MoviesContract.ReviewEntry.buildReviewData(), reviews);
                    return true;
                } else {
                    return false;
                }
            case TYPE_TRAILERS:
                Log.d(TAG, "requesting API to get trailers");
                ContentValues[] trailers = retrieveTrailers(params[0]);
                if (trailers != null && trailers.length > 0) {
                    mContext.getContentResolver().bulkInsert(MoviesContract.TrailerEntry.buildTrailerData(), trailers);
                    return true;
                } else {
                    return false;
                }
            case TYPE_CAST:
                Log.d(TAG, "requesting API to get cast");
                ContentValues[] cast = retrieveCast(params[0]);
                if (cast != null && cast.length > 0) {
                    mContext.getContentResolver().bulkInsert(MoviesContract.CastEntry.buildCastData(), cast);
                    return true;
                } else {
                    return false;
                }
            case TYPE_ACTOR:
                Log.d(TAG, "requesting API to get actor info");
                ContentValues values = retrieveActorInfo(params[0]);
                if (values != null) {
                    mContext.getContentResolver().insert(MoviesContract.ActorsEntry.buildActorsData(), values);
                    return true;
                } else
                    return false;
            case TYPE_MOVIES:
                ContentValues[] movies = DataHelper.retrieveMoviesData(mContext, categoryName, params[0]);
                if (movies != null) {
                    int categoryId = Utils.getCategoryByName(mContext, categoryName);
                    mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.buildMoviesDataWithCategory(categoryId), movies);
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean value) {
        super.onPostExecute(value);
        if (mCallback != null) {
            mCallback.onDataRetrieved(value);
        }
    }

    private ContentValues[] retrieveReviews(int movieId) {
        HttpURLConnection urlConnection = null;
        ArrayList<Review> mReviews = new ArrayList<>();
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
        ArrayList<Trailer> mTrailers = new ArrayList<>();
        ;
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

    private ContentValues[] retrieveCast(int movieId) {

        HttpURLConnection urlConnection = null;
        ArrayList<CastActor> castActors = new ArrayList<>();
        ;
        try {
            //Composing url to request data
            URL url = new URL(mContext.getString(R.string.tmdb_api_url) + movieId + "/credits" + mContext.getString(R.string.tmdb_api_key));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Getting string from input stream
            String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

            //Parse Data
            castActors = DataHelper.parseCastJson(mContext, movieId, jsonResult);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return DataHelper.prepareToInsertCast(castActors);
    }


    private ContentValues retrieveActorInfo(int actorId) {

        HttpURLConnection urlConnection = null;
        Actor actor = null;

        try {
            //Composing url to request data
            URL url = new URL(mContext.getString(R.string.tmdb_api_url_person) + actorId + mContext.getString(R.string.tmdb_api_key));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Getting string from input stream
            String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

            //Parse Data
            actor = DataHelper.parseActorJson(mContext, jsonResult);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return DataHelper.prepareToInsertActor(actor);
    }
}
