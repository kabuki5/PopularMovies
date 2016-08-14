package com.benavides.ramon.popularmovies.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.benavides.ramon.popularmovies.MainActivity;
import com.benavides.ramon.popularmovies.MovieDetailActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.MoviesCursorAdapter;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorInterface;
import com.benavides.ramon.popularmovies.interfaces.TmdbApiTaskListener;
import com.benavides.ramon.popularmovies.tasks.FecthMoviesTask;
import com.benavides.ramon.popularmovies.utils.CursorUtils;
import com.benavides.ramon.popularmovies.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment class to show the movies into a grid view
 */
public class MoviesFragment extends Fragment implements TmdbApiTaskListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;

    private static final String MOVIE_PARAM = "movie_data";
    private static final String GRID_POSITION = "grv_position";
    private static final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.movies_grv)
    GridView mGridView;

    private MovieSelectorInterface mCallback;
    private ArrayList<Movie> mMoviesData;
    private MoviesCursorAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MovieSelectorInterface) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MovieSelectorInterface) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_fragment_layout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initFavLoader(MOVIES_LOADER, null, this);

        String currentSortTypeSelected = Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref));

//        Restoring instance state. I don't care what sort type is established cause I have data array list from bundle.
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_PARAM)) {
            mMoviesData = savedInstanceState.getParcelableArrayList(MOVIE_PARAM);
            updateGridView(mMoviesData);
            mGridView.smoothScrollToPosition(savedInstanceState.getInt(GRID_POSITION));
            Log.v("RBM", "Restoring state!!");
        } else {
            if (currentSortTypeSelected.equals(getString(R.string.favorites_low))) {
                Log.v("RBM", "Init favs loader!!");
                initFavLoader();
            } else {
                Log.v("RBM", "Calling api task!!");
                new FecthMoviesTask(this, getActivity()).execute(currentSortTypeSelected);//pass popular/top_rated parameter
            }
        }
//

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new MoviesCursorAdapter(getActivity(), null, true);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_PARAM, mMoviesData);
        outState.putInt(GRID_POSITION, mGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by:

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.choose_sort_by_dialog);
                dialog.show();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selection = "";

                        int id = v.getId();

                        switch (id) {
                            case R.id.popular_dialog_button:
                                new FecthMoviesTask(MoviesFragment.this, getActivity()).execute(getString(R.string.popular_low));//pass popular/top_rated parameter
                                selection = getString(R.string.popular_low);
                                break;
                            case R.id.top_rated_dialog_button:
                                new FecthMoviesTask(MoviesFragment.this, getActivity()).execute(getString(R.string.top_rated_low));
                                selection = getString(R.string.top_rated_low);
                                break;
                            case R.id.favorites_dialog_button:
                                initFavLoader();
                                selection = getString(R.string.favorites_low);
                                break;
                        }

                        Utils.writeStringPreference(getActivity(), getString(R.string.sort_by_pref), selection);
                        dialog.dismiss();
                    }
                };

                dialog.findViewById(R.id.popular_dialog_button).setOnClickListener(clickListener);
                dialog.findViewById(R.id.top_rated_dialog_button).setOnClickListener(clickListener);
                dialog.findViewById(R.id.favorites_dialog_button).setOnClickListener(clickListener);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //    APIDataTaskListener callback methods

    @Override
    public void onRequestSuccess(ArrayList<Movie> movies) {
        /*for (Movie movie : movies) {
            Log.v(TAG, movie.toString());
        }*/
        mMoviesData = movies;
        updateGridView(movies);
    }

    @Override
    public void onRequestError() {
        //TODO => Manage request API error. Maybe show a dialog with info.
    }


    //Gridview methods

    private void updateGridView(ArrayList<Movie> movies) {
        mAdapter.swapCursor(CursorUtils.convertMovieArrayListToCursor(movies));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((Cursor) parent.getItemAtPosition(position));

        if (cursor != null) {
            /* Since I have two ways to obtain data, favorites from DB and popular and top rated from API, I can't pass a cursor to DetailFragment
            * and I need homogenize with passing a Movie object instead.
            */
            Movie movie = CursorUtils.getMovieFromCursor(cursor);

            if (mCallback != null) {
                mCallback.onMovieSelected(movie);
            }
        }
//        ContentValues contentValues = new ContentValues();
//        DatabaseUtils.cursorRowToContentValues(cursor,contentValues);
//        getActivity().getContentResolver().insert(MoviesContract.MovieEntry.buildMoviesData(),contentValues);
    }


    private void initFavLoader() {
        if (getLoaderManager().getLoader(MOVIES_LOADER) != null) {
            restartLoader();
        } else {
            getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

//    Loader callback methods

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = MoviesContract.MovieEntry.COLUMN_RATING + " DESC";

        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.buildMoviesData(), MoviesContract.MovieEntry.MOVIES_PROJECTION, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

//        Log.d("RBM", "onLoadFinished");

        mAdapter.swapCursor(data);
        mMoviesData = CursorUtils.convertMovieCursorToArrayList(data);

    }

    @Override
    public void onLoaderReset(Loader loader) {
//        mAdapter.swapCursor(null);
    }

}
