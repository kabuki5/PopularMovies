package com.benavides.ramon.popularmovies.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.interfaces.TmdbApiTaskListener;
import com.benavides.ramon.popularmovies.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class to retrieve data from the movie data base API in background thread.
 */
public class TmdbApiTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private TmdbApiTaskListener mCallback;
    private Context mContext;

    public TmdbApiTask(TmdbApiTaskListener callback, Context context){
        this.mCallback = callback;
        this.mContext = context;
    }


    /**
     * Method to parse the movie data base json response
     * @param json String with json content
     * @return
     */
    private ArrayList<Movie> parseJson(String json) throws JSONException{

        ArrayList<Movie> result = new ArrayList<>();

        JSONObject movieData = new JSONObject(json);
        JSONArray movies = movieData.getJSONArray("results");
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movieObject = movies.getJSONObject(i);
            Movie movie = new Movie();
            movie.setOriginalTitle(movieObject.getString("original_title"));
            movie.setPoster(mContext.getString(R.string.tmdb_poster_base_url)+"w185"+movieObject.getString("poster_path"));
            movie.setBackdrop(mContext.getString(R.string.tmdb_poster_base_url)+"w500"+movieObject.getString("backdrop_path"));
            movie.setRating(movieObject.getDouble("vote_average"));
            movie.setReleaseDate(movieObject.getString("release_date"));
            movie.setSynopsis(movieObject.getString("overview"));
            result.add(movie);
        }

        return result;

    }


    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        ArrayList<Movie> movies = null;
        try{
            String movieChoice = params[0];

            if(movieChoice == null){
                //
                return null;
            }

            Log.v("RBM",mContext.getString(R.string.tmdb_api_url)+ movieChoice + mContext.getString(R.string.tmdb_api_key));

            //Composing url to request data
            URL url = new URL(mContext.getString(R.string.tmdb_api_url)+ movieChoice + mContext.getString(R.string.tmdb_api_key));

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Getting string from input stream
            String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

            //Parse Data
            movies = parseJson(jsonResult);



        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);

        //Callback
        if(mCallback!=null){
            if(movies != null)
                mCallback.onRequestSuccess(movies);
            else
                mCallback.onRequestError();
        }

    }
}
