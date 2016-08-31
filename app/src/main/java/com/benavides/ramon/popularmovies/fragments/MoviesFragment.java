package com.benavides.ramon.popularmovies.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.MoviesCursorAdapter;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorListener;
import com.benavides.ramon.popularmovies.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment class to show the movies into a grid view
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;

    private static final String GRID_POSITION = "grv_position";
    private static final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.movies_grv)
    GridView mGridView;

    private MovieSelectorListener mCallback;
    private MoviesCursorAdapter mAdapter;
    // private MovieDataReceiver mDataReceiver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MovieSelectorListener) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MovieSelectorListener) activity;
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

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupActionBar();

        mAdapter = new MoviesCursorAdapter(getActivity(), null, true);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
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
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.choose_sort_by_dialog);
                dialog.show();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selection;
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
                            default:
                                selection = getString(R.string.popular_low);
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
    private void updateGridView(Cursor movies) {
        mAdapter.swapCursor(movies);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((Cursor) parent.getItemAtPosition(position));

        if (cursor != null) {
            if (mCallback != null) {
                mCallback.onMovieSelected(cursor.getInt(MoviesContract.MovieEntry.MOVIES_COLUMN_ID));
            }
        }
    }


//    Loader callback methods

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = MoviesContract.MovieEntry.COLUMN_RATING + " DESC";
        String category = Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref));
        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.buildMoviesDataWithCategory(category), MoviesContract.MovieEntry.MOVIES_PROJECTION,
                null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        updateGridView(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
//        mAdapter.swapCursor(null);
    }


    public void updateData() {
        setupActionBar();
        mGridView.smoothScrollToPosition(0);
        getLoaderManager().restartLoader(MOVIES_LOADER, null, MoviesFragment.this);
    }


    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            String title;
            String lastCategory = Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref));
            if (lastCategory.equals(getString(R.string.popular_low))) {
                title = getString(R.string.popular);
            } else if (lastCategory.equals(getString(R.string.top_rated_low))) {
                title = getString(R.string.top_rated);
            } else {
                title = getString(R.string.favorites);
            }
            actionBar.setTitle(title);
        }
    }
}
