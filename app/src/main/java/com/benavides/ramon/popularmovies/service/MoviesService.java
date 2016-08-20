package com.benavides.ramon.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.utils.CursorUtils;
import com.benavides.ramon.popularmovies.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MoviesService extends IntentService {

    public static final String TYPE_PARAM = "query_param";

    public static final String MOVIE_DATA_ACTION_INCOMING = "com.benavides.ramon.popularmovies.ACTION_DATA_INCOMING";
    public static final String MOVIE_DATA_ACTION_ERROR = "com.benavides.ramon.popularmovies.ACTION_DATA_ERROR";
    public static final String MOVIES_PARAM = "movies_data";

    public MoviesService() {
        super("MoviesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent resultIntent = new Intent();

        HttpURLConnection urlConnection = null;
        ArrayList<Movie> movies = null;
        try {
            String movieChoice = intent.getStringExtra(TYPE_PARAM);

            if (movieChoice == null) {
                //
                resultIntent.setAction(MOVIE_DATA_ACTION_ERROR);
                sendBroadcast(resultIntent);
            } else {
                //Composing url to request data
                URL url = new URL(getString(R.string.tmdb_api_url) + movieChoice + getString(R.string.tmdb_api_key));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Getting string from input stream
                String jsonResult = Utils.readInputStream(urlConnection.getInputStream());

                //Parse Data
                movies = parseJson(jsonResult);

//      insert data into database
                getContentResolver().bulkInsert(MoviesContract.MovieEntry.buildMoviesDataWithCategory(movieChoice), CursorUtils.prepareToInsertMovies(movies));

                resultIntent.setAction(MOVIE_DATA_ACTION_INCOMING);
                sendBroadcast(resultIntent);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to parse the movie data base json response
     *
     * @param json String with json content
     * @return
     */
    private ArrayList<Movie> parseJson(String json) throws JSONException {

        ArrayList<Movie> result = new ArrayList<>();

        JSONObject movieData = new JSONObject(json);
        JSONArray movies = movieData.getJSONArray("results");
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movieObject = movies.getJSONObject(i);
            Movie movie = new Movie();
            movie.setId(movieObject.getInt("id"));
            movie.setOriginalTitle(movieObject.getString("original_title"));
            movie.setPoster(getString(R.string.tmdb_poster_base_url) + "w185" + movieObject.getString("poster_path"));
            movie.setBackdrop(getString(R.string.tmdb_poster_base_url) + "w500" + movieObject.getString("backdrop_path"));
            movie.setRating(movieObject.getDouble("vote_average"));
            movie.setReleaseDate(movieObject.getString("release_date"));
            movie.setSynopsis(movieObject.getString("overview"));
            result.add(movie);
//            Log.d("RBM","MOvie => "+movie.toString());
        }

        return result;

    }


}


