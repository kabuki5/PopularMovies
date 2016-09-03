package com.benavides.ramon.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.ActorInfoActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.CastCursorAdapter;
import com.benavides.ramon.popularmovies.cviews.RoundedImageView;
import com.benavides.ramon.popularmovies.data.CastActor;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DataTaskListener;
import com.benavides.ramon.popularmovies.interfaces.DetailContentChangeListener;
import com.benavides.ramon.popularmovies.tasks.ObtainDataTask;
import com.benavides.ramon.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SynopsisFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, DataTaskListener, View.OnClickListener {
    private static final int MOVIE_LOADER = 1;
    private static final int CAST_LOADER = 7;
    private static final int MAX_ACTORS = 11;

    private DetailContentChangeListener mCallback;

    @BindView(R.id.synopsis_tev)
    TextView synopsisTev;
    @BindView(R.id.rating_bar)
    RatingBar userRatingBar;
    @BindView(R.id.release_date_tev)
    TextView releaseDateTev;
    @BindView(R.id.view_synopsis_cast_ll)
    LinearLayout mCastGrl;

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
        //init loaders
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(CAST_LOADER, null, castLoaderCallbacks);
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

        Drawable drawable = userRatingBar.getProgressDrawable();
        drawable.setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DetailContentChangeListener) context;
    }

    //Populate content
    public void populateSynopsis(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            synopsisTev.setText(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_SYNOPSIS));
            userRatingBar.setRating(((float) cursor.getDouble(MoviesContract.MovieEntry.MOVIES_COLUMN_RATING)) / 2);
            releaseDateTev.setText(Utils.formatReleaseDate(cursor.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_RELEASE_DATE)));
        }
    }

    private void populateCast(Cursor cursor) {
        mCastGrl.removeAllViews();
        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            do {

                int layout = i % 2 == 0 ? R.layout.item_actor : R.layout.item_actor2;
                String character = getActivity().getString(R.string.as) + cursor.getString(MoviesContract.CastEntry.CAST_COLUMN_CHARACTER);

                View castView = LayoutInflater.from(getActivity()).inflate(layout, null);
                ((TextView) castView.findViewById(R.id.item_actor_name_tev)).setText(cursor.getString(MoviesContract.CastEntry.CAST_COLUMN_NAME));
                ((TextView) castView.findViewById(R.id.item_actor_character_tev)).setText(character);
                Picasso.with(getActivity()).load(cursor.getString(MoviesContract.CastEntry.CAST_COLUMN_PICTURE)).into((RoundedImageView) castView.findViewById(R.id.item_actor_thumb));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((int) getActivity().getResources().getDimension(R.dimen.item_actor_height)));
                int margin = (int) getResources().getDimension(R.dimen.dimen_cardview_margin);

                params.setMargins(margin, margin, margin, 0);

                castView.setLayoutParams(params);
                castView.setTag(cursor.getInt(MoviesContract.CastEntry.CAST_COLUMN_ID));
                mCastGrl.addView(castView);
                castView.setOnClickListener(this);
                i++;
            } while (cursor.moveToNext() && i < MAX_ACTORS);
        }
    }


    @Override
    public String getTitle() {
        return "Synopsis";
    }


    // Movie loader callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.buildMoviesData(), MoviesContract.MovieEntry.MOVIES_PROJECTION,
                MoviesContract.MovieEntry._ID + " =?", new String[]{Integer.toString(movieID)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            if (data.moveToFirst()) {
                if (mCallback != null)
                    mCallback.onContentChanged(data.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_TITLE),
                            data.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_BACKDROP));
                populateSynopsis(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //CastActor loader callbacks
    LoaderManager.LoaderCallbacks castLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), MoviesContract.CastEntry.buildCastData(), MoviesContract.CastEntry.CAST_PROJECTION,
                    MoviesContract.CastEntry.COLUMN_MOVIE_ID + " =?", new String[]{Integer.toString(movieID)}, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.getCount() == 0) {
                new ObtainDataTask(getActivity(), SynopsisFragment.this, ObtainDataTask.TYPE_CAST).execute(movieID);
            } else {
                populateCast(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    // Obtain task callback
    @Override
    public void onDataRetrieved(boolean hasReceiveData) {
        if (isAdded() && hasReceiveData)
            getLoaderManager().restartLoader(CAST_LOADER, null, castLoaderCallbacks);
    }

    @Override
    public void onClick(View v) {
        int actorId = (int) v.getTag();
        Intent actorInfoIntent = new Intent(getActivity(), ActorInfoActivity.class);
        actorInfoIntent.putExtra(getString(R.string.actor_id_param),actorId);
        startActivity(actorInfoIntent);
    }
}
