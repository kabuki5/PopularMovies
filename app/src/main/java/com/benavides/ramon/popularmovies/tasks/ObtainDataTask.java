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

    public static final int TYPE_REVIEWS = 1;
    public static final int TYPE_TRAILERS = 2;
    private final int mType;

    public ObtainDataTask(Context context, DataTaskListener callback, int type ) {
        this.mContext = context;
        this.mCallabck = callback;
        this.mType = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mReviews = new ArrayList<>();
        mTrailers = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Integer... params) {

            switch (mType){
                case TYPE_REVIEWS:
                    ContentValues[] reviews = retrieveReviews(params[0]);
                    if(reviews!=null && reviews.length>0)
                        mContext.getContentResolver().bulkInsert(MoviesContract.ReviewEntry.buildReviewData(), reviews);
                    break;
                case TYPE_TRAILERS:
                    ContentValues[] trailers = retrieveTrailers(params[0]);
                    if(trailers!=null && trailers.length>0)
                        mContext.getContentResolver().bulkInsert(MoviesContract.TrailerEntry.buildTrailerData(), trailers);
                    break;
            }
        return null;
    }

    @Override
    protected void onPostExecute(Void value) {
        super.onPostExecute(value);

        if(mCallabck!= null){
            mCallabck.onDataRetrieved();
        }
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
