package com.benavides.ramon.popularmovies.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.MoviesCursorAdapter;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorInterface;
import com.benavides.ramon.popularmovies.service.MoviesService;
import com.benavides.ramon.popularmovies.utils.CursorUtils;
import com.benavides.ramon.popularmovies.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment class to show the movies into a grid view
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;

    private static final String MOVIE_PARAM = "movie_data";
    private static final String GRID_POSITION = "grv_position";
    private static final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.movies_grv)
    GridView mGridView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private MovieSelectorInterface mCallback;
    private MoviesCursorAdapter mAdapter;
    private MovieDataReceiver mDataReceiver;

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

    @Override
    public void onResume() {
        super.onResume();
        mDataReceiver = new MovieDataReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MoviesService.MOVIE_DATA_ACTION_INCOMING);
        intentFilter.addAction(MoviesService.MOVIE_DATA_ACTION_ERROR);
        getActivity().registerReceiver(mDataReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDataReceiver != null) {
            getActivity().unregisterReceiver(mDataReceiver);
        }
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


//        String currentSortTypeSelected = Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref));
//            requestToService(currentSortTypeSelected);//pass popular/top_rated parameter

//        Restoring instance state. I don't care what sort type is established cause I have data array list from bundle.
//        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_PARAM)) {
//            mGridView.smoothScrollToPosition(savedInstanceState.getInt(GRID_POSITION));
//            Log.v("RBM", "Restoring state!!");
//        } else {
            Log.v("RBM", "Calling api task!!");
//            requestToService(currentSortTypeSelected);//pass popular/top_rated parameter
//        }

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
//
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new MoviesCursorAdapter(getActivity(), null, true);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestToService(Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref)));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
                                selection = getString(R.string.popular_low);
                                break;
                            case R.id.top_rated_dialog_button:
                                selection = getString(R.string.top_rated_low);
                                break;
                            case R.id.favorites_dialog_button:
                                selection = getString(R.string.favorites_low);
                                break;
                        }

                        Utils.writeStringPreference(getActivity(), getString(R.string.sort_by_pref), selection);
                        dialog.dismiss();
                        updateData();
                    }
                };

                dialog.findViewById(R.id.popular_dialog_button).setOnClickListener(clickListener);
                dialog.findViewById(R.id.top_rated_dialog_button).setOnClickListener(clickListener);
                dialog.findViewById(R.id.favorites_dialog_button).setOnClickListener(clickListener);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //Gridview methods

    private void updateGridView(ArrayList<Movie> movies) {
        mAdapter.swapCursor(CursorUtils.convertMovieArrayListToCursor(movies));
    }

    private void updateGridView(Cursor movies) {
        mAdapter.swapCursor(movies);
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
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor,contentValues);
        getActivity().getContentResolver().insert(MoviesContract.MovieEntry.buildMoviesData(),contentValues);
    }


//    Loader callback methods

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = MoviesContract.MovieEntry.COLUMN_RATING + " DESC";

        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.buildMoviesData(), MoviesContract.MovieEntry.MOVIES_PROJECTION,
                null, new String[]{Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref))}, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        updateGridView(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
//        mAdapter.swapCursor(null);
    }


    //    service methods
    private void requestToService(String typeSelection) {
        Intent intentToService = new Intent(getActivity(), MoviesService.class);
        intentToService.putExtra(MoviesService.TYPE_PARAM, typeSelection);
        getActivity().startService(intentToService);
    }


    class MovieDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(MoviesService.MOVIE_DATA_ACTION_INCOMING)) {
                updateData();
            } else if (action != null && action.equals(MoviesService.MOVIE_DATA_ACTION_ERROR)) {
                // NO data received
            }
        }
    }

    public void updateData(){
        getLoaderManager().restartLoader(MOVIES_LOADER, null, MoviesFragment.this);
    }
}
