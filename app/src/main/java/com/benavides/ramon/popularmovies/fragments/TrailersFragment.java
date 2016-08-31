package com.benavides.ramon.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.TrailersCursorAdapter;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DataTaskListener;
import com.benavides.ramon.popularmovies.tasks.ObtainDataTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, DataTaskListener, AdapterView.OnItemClickListener {

    private static final int TRAILERS_LOADER = 4;
    private int mMovieId;
    private TrailersCursorAdapter mAdapter;

    @BindView(R.id.trailers_liv)
    ListView mLiv;

    NestedScrollView mNoDataView;

    public static TrailersFragment newInstance(int movieId) {

        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movieId);

        TrailersFragment fragment = new TrailersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mMovieId = getArguments().getInt(MOVIE_PARAM);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trailers_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoDataView = (NestedScrollView)view.findViewById(R.id.no_data_view);

        mAdapter = new TrailersCursorAdapter(getActivity(), null, true);
        mLiv.setAdapter(mAdapter);
        mLiv.setOnItemClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLiv.setNestedScrollingEnabled(true);
        }
    }


    @Override
    public String getTitle() {
        return null;
    }

    //  Loader callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesContract.TrailerEntry.buildTrailerData(), MoviesContract.TrailerEntry.TRAILER_PROJECTION,
                MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + "=?", new String[]{Integer.toString(mMovieId)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            new ObtainDataTask(getActivity(), this, ObtainDataTask.TYPE_TRAILERS).execute(mMovieId);
        } else {
            populateContent(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    //  DataTaskListener method
    @Override
    public void onDataRetrieved(boolean hasReceiveData) {
        if (isAdded() && hasReceiveData) {
            mNoDataView.setVisibility(View.GONE);
            getLoaderManager().restartLoader(TRAILERS_LOADER, null, this);
        } else if (!hasReceiveData) {
            mNoDataView.setVisibility(View.VISIBLE);
        }
    }

    private void populateContent(Cursor data) {
        //insert data into listview
        mAdapter.swapCursor(data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((Cursor) parent.getItemAtPosition(position));
        if (cursor != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cursor.getString(MoviesContract.TrailerEntry.TRAILER_COLUMN_SOURCE)));
            startActivity(intent);
        }
    }
}
