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
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.benavides.ramon.popularmovies.AboutActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.MoviesCursorAdapter;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DataTaskListener;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorListener;
import com.benavides.ramon.popularmovies.tasks.ObtainDataTask;
import com.benavides.ramon.popularmovies.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment class to show the movies into a grid view
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener, DataTaskListener {

    private static final int MOVIES_LOADER = 0;

    private static final String GRID_POSITION = "grv_position";
    private static final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.movies_grv)
    GridView mGridView;

    FloatingActionButton mMoreMoviesFab;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    mGridView.setOnScrollListener(MoviesFragment.this);
                }
            });
        }
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

        mMoreMoviesFab = (FloatingActionButton) view.findViewById(R.id.fab_more_movies);
        setupActionBar();

        mAdapter = new MoviesCursorAdapter(getActivity(), null, true);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        mMoreMoviesFab.setOnClickListener(moreMoviesFabOnClick);
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
            case R.id.action_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
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

        String sortOrder = MoviesContract.MovieEntry.COLUMN_ORDER + " ASC";
        String category = Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref));
        int categoryID = Utils.getCategoryByName(getActivity(), category);
        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.buildMoviesDataWithCategory(categoryID), MoviesContract.MovieEntry.MOVIES_PROJECTION,
                null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /*data.moveToFirst();
        do{
            Log.d("RBM","MOVIE => "+data.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE));
            Log.d("RBM","ORDER => "+data.getInt(MoviesContract.MovieEntry.MOVIES_COLUMN_ORDER));
            Log.d("RBM","****************************************************************");
        }while(data.moveToNext());*/

        updateGridView(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.changeCursor(null);
    }


    public void updateData() {
        setupActionBar();
        mGridView.setSelection(0);
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && !Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref)).equals(getString(R.string.favorites_low))) {
            Utils.revealAnimateView(mMoreMoviesFab, true);
        } else {
            Utils.revealAnimateView(mMoreMoviesFab, false);
        }
    }

    View.OnClickListener moreMoviesFabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int numMovies = mAdapter.getCount();
            int page = (numMovies / 20) + 1;
            String currentCategory = Utils.readStringPreference(getActivity(), getString(R.string.sort_by_pref));
            new ObtainDataTask(getActivity(), MoviesFragment.this, ObtainDataTask.TYPE_MOVIES, currentCategory).execute(page);


            /**
             * - get cursor item count from adapter
             * - (count / 20) + 1 = page to request
             * - add request to Obtain data task
             * - on callback ->  restart loader
             */
        }
    };

    // Obtain movies callback
    @Override
    public void onDataRetrieved(boolean hasReceiveData) {
        if (hasReceiveData) {
            // getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
            mMoreMoviesFab.setVisibility(View.GONE);
        }
    }
}
