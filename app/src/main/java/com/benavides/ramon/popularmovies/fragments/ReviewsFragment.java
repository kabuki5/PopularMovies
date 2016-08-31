package com.benavides.ramon.popularmovies.fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.ReviewsCursorAdapter;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DataTaskListener;
import com.benavides.ramon.popularmovies.tasks.ObtainDataTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, DataTaskListener {

    private static final int REVIEWS_LOADER = 3;
    private int mMovieId;

    @BindView(R.id.reviews_liv)
    ListView mLiv;

    private ReviewsCursorAdapter mAdapter;

    public static ReviewsFragment newInstance(int movieId) {
        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movieId);

        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mMovieId = getArguments().getInt(MOVIE_PARAM);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ReviewsCursorAdapter(getActivity(), null, true);
        mLiv.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLiv.setNestedScrollingEnabled(true);
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    //   Loader methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesContract.ReviewEntry.buildReviewData(), MoviesContract.ReviewEntry.REVIEW_PROJECTION,
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + "=?", new String[]{Integer.toString(mMovieId)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            new ObtainDataTask(getActivity(), this, ObtainDataTask.TYPE_REVIEWS).execute(mMovieId);
        } else {
            populateContent(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //  DataTaskListener method
    @Override
    public void onDataRetrieved() {
        if(isAdded())
            getLoaderManager().restartLoader(REVIEWS_LOADER, null, this);
    }

    private void populateContent(Cursor data) {
        //insert data into listview
        mAdapter.swapCursor(data);
    }

}
