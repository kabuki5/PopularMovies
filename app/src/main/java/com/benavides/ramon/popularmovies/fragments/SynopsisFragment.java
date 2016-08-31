package com.benavides.ramon.popularmovies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DetailContentChangeListener;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SynopsisFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int MOVIE_LOADER = 1;

    private DetailContentChangeListener mCallback;

    @BindView(R.id.synopsis_tev)
    TextView synopsisTev;
    @BindView(R.id.user_rating_tev)
    TextView userRatingTev;
    @BindView(R.id.release_date_tev)
    TextView releaseDateTev;

    private int movieID;

    public static SynopsisFragment newInstance(int movieID) {
        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movieID);
        SynopsisFragment fragment = new SynopsisFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        movieID = getArguments().getInt(MOVIE_PARAM);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_synopsis_frg, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DetailContentChangeListener)context;
    }

    //Populate content
    public void populateContent(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            synopsisTev.setText(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_SYNOPSIS));
            userRatingTev.setText(NumberFormat.getNumberInstance().format(cursor.getDouble(MoviesContract.MovieEntry.MOVIES_COLUMN_RATING)));
            releaseDateTev.setText(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_RELEASE_DATE));
        }
    }


    @Override
    public String getTitle() {
        return "Synopsis";
    }


    // Loader methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.buildMoviesData(), MoviesContract.MovieEntry.MOVIES_PROJECTION,
                MoviesContract.MovieEntry._ID + " =?", new String[]{Integer.toString(movieID)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            if(data.moveToFirst()){
                if(mCallback!=null)
                    mCallback.onContentChanged(data.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE),
                            data.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_BACKDROP));
                populateContent(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
