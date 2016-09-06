package com.benavides.ramon.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.interfaces.DataTaskListener;
import com.benavides.ramon.popularmovies.tasks.ObtainDataTask;
import com.benavides.ramon.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Function:
 */
public class ActorInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DataTaskListener {

    private static final String ACTOR_PARAM = "actor_id";
    private static final String TWO_PANE_PARAM = "two_pane";
    private static final int LOADER_ACTOR_INFO = 8;

    private int mActorId;
    private boolean mTwoPane;

    @BindView(R.id.main_container)
    RelativeLayout mInfoContainer;
    @BindView(R.id.no_data_view)
    NestedScrollView mNoDataView;
    @BindView(R.id.picture)
    ImageView mPictureImv;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.birthday)
    TextView mBirthday;
    @BindView(R.id.deathday)
    TextView mDeathDay;
    @BindView(R.id.place)
    TextView mPlace;
    @BindView(R.id.biography)
    TextView mBiography;
    @BindView(R.id.deathday_title)
    TextView mDeathdayTitle;

    public static ActorInfoFragment newInstance(int actorId, boolean twoPane) {

        Bundle args = new Bundle();
        args.putInt(ACTOR_PARAM, actorId);
        args.putBoolean(TWO_PANE_PARAM, twoPane);
        ActorInfoFragment fragment = new ActorInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ACTOR_INFO, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_actor_info_frg, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTwoPane = getArguments().getBoolean(TWO_PANE_PARAM);
        mActorId = getArguments().getInt(ACTOR_PARAM);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Loader callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesContract.ActorsEntry.buildActorsData(),
                MoviesContract.ActorsEntry.ACTORS_PROJECTION, MoviesContract.ActorsEntry._ID + " =?",
                new String[]{Integer.toString(mActorId)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            new ObtainDataTask(getActivity(), this, ObtainDataTask.TYPE_ACTOR).execute(mActorId);
        } else {
            populateData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //Obtain data task callback
    @Override
    public void onDataRetrieved(boolean hasReceiveData) {
        if (isAdded() && hasReceiveData) {
            mNoDataView.setVisibility(View.GONE);
            getLoaderManager().restartLoader(LOADER_ACTOR_INFO, null, this);
        } else if (!hasReceiveData) {
            mInfoContainer.setVisibility(View.GONE);
            mNoDataView.setVisibility(View.VISIBLE);
        }
    }

    // show info
    private void populateData(Cursor data) {
        data.moveToFirst();

        if (!mTwoPane)
            setupActionBar(data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_NAME));

        String name = data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_NAME);
        String birthday = data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_BIRTHDAY);
        String deathDay = data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_DEATHDAY);
        String place = data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_PLACE);
        String biography = data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_BIOGRAPHY);
        String noData = getString(R.string.no_data_found);


        Picasso.with(getActivity()).load(data.getString(MoviesContract.ActorsEntry.ACTORS_COLUMN_PICTURE))
                .error(R.mipmap.ic_default_photo).into(mPictureImv);
        if (name == null || name.isEmpty() || name.equals("null"))
            name = noData;
        mName.setText(name);

        if (birthday == null || birthday.isEmpty() || birthday.equals("null"))
            birthday = noData;
        mBirthday.setText(birthday);

        if (deathDay != null && !deathDay.isEmpty() && !deathDay.equals("null")) {
            mDeathDay.setVisibility(View.VISIBLE);
            mDeathdayTitle.setVisibility(View.VISIBLE);
            mDeathDay.setText(deathDay);
        }

        if (place == null || place.isEmpty() || place.equals("null"))
            place = noData;
        mPlace.setText(place);

        if (biography == null || biography.isEmpty() || biography.equals("null"))
            biography = noData;
        mBiography.setText(biography);

    }

    private void setupActionBar(String name) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(name);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}
