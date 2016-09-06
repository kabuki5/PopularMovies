package com.benavides.ramon.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.MovieDetailActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.adapters.SectionsPagerAdapter;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Contains CoordinatorLayout with viewpager
 */
public class MovieDetailContainerFragment extends Fragment implements View.OnClickListener {

    private static final String MOVIE_PARAM = "movie";
    private static final int MAX_FRGS = 3;
    private static final int FRG_DETAIL = 0;
    private static final int FRG_REVIEWS = 1;
    private static final int FRG_TRAILERS = 2;
    private static final String TITLE_PARAM = "title";
    private static final String BACKDROP_PARAM = "backdrop";

    private boolean isFavorite;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.backdrop)
    ImageView backdropImv;

    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.favorites_fab)
    FloatingActionButton mFab;

    private String mTitle;
    private String mBackdrop;
    private Fragment[] mFragments;
    private int movieID;
    private SectionsPagerAdapter mAdapter;


    public static MovieDetailContainerFragment newInstance(int movie) {
        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movie);
        MovieDetailContainerFragment fragment = new MovieDetailContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String title = savedInstanceState.getString(TITLE_PARAM);
            String backdrop = savedInstanceState.getString(BACKDROP_PARAM);
            movieID = savedInstanceState.getInt(MOVIE_PARAM);
            updateHeader(title, backdrop);
        }
        super.onActivityCreated(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.container_detail_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle extras;

        if (savedInstanceState != null) {
            movieID = savedInstanceState.getInt(MOVIE_PARAM);
        } else if (getArguments() != null) {
            movieID = getArguments().getInt(MOVIE_PARAM);
        } else if (getActivity().getIntent() != null && (extras = getActivity().getIntent().getExtras()) != null) {// from intent
            movieID = extras.getInt(getString(R.string.movie_intent_tag));
        }

        //Setting up toolbar
        Toolbar toolbar = ButterKnife.findById(view, R.id.toolbar);

        //Managing action bar navigation back button state
        if (getActivity() instanceof MovieDetailActivity) {
            ActionBar actionBar = ((MovieDetailActivity) getActivity()).getSupportActionBar();
            if (actionBar == null)
                ((MovieDetailActivity) getActivity()).setSupportActionBar(toolbar);
            actionBar = ((MovieDetailActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {// two pane mode
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar == null)
                ((MovieDetailActivity) getActivity()).setSupportActionBar(toolbar);
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);

        }



        //        Managing toolbar back button behavior
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //     Setting up tab viewpager
        mAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), initFragments(movieID));

        mViewpager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewpager);

        setupTabs();

        mFab.setOnClickListener(this);
    }


    private void setupTabs() {
        TypedArray images = getResources().obtainTypedArray(R.array.tab_icons);
        String[] titles = getResources().getStringArray(R.array.tab_titles);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_tab, null);
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                ((ImageView) view.findViewById(R.id.tab_view_img)).setImageResource(images.getResourceId(i, R.mipmap.ic_launcher));
                ((TextView) view.findViewById(R.id.tab_view_txt)).setText(titles[i]);
                tab.setCustomView(view);
            }
        }
    }

    private Fragment[] initFragments(int movieID) {
        mFragments = new Fragment[MAX_FRGS];
        mFragments[FRG_DETAIL] = SynopsisFragment.newInstance(movieID);
        mFragments[FRG_REVIEWS] = ReviewsFragment.newInstance(movieID);
        mFragments[FRG_TRAILERS] = TrailersFragment.newInstance(movieID);

        return mFragments;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TITLE_PARAM, mTitle);
        outState.putString(BACKDROP_PARAM, mBackdrop);
        outState.putInt(MOVIE_PARAM, movieID);
        super.onSaveInstanceState(outState);
    }

    public void updateHeader(String title, String backdropURL) {
        mTitle = title;
        mBackdrop = backdropURL;

        collapsingToolbarLayout.setTitle(title);

        Picasso.with(getActivity())
                .load(backdropURL)
                //.placeholder(R.drawable.ic_movie)
                .error(R.drawable.cloud)
                .into(backdropImv);

        //Check if it's favorite
        isFavoriteMovie();
        updateFab();

    }

    private void updateFab() {
        int imageResource;
        if (isFavorite)
            imageResource = R.mipmap.ic_heart_full;
        else
            imageResource = R.mipmap.ic_heart;

        mFab.setImageResource(imageResource);
    }

    @Override
    public void onClick(View v) {
        Cursor cursor = getContext().getContentResolver().query(MoviesContract.MovieEntry.buildMoviesData(), null, MoviesContract.MovieEntry._ID + " =?", new String[]{Integer.toString(movieID)}, null);
        if (cursor != null && cursor.moveToFirst())
            addRemoveFavorite(cursor);
    }

    private void addRemoveFavorite(Cursor cursor) {
        // insert favorite or remove favorite
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        getActivity().getContentResolver().insert(MoviesContract.MovieEntry.buildMoviesData(), contentValues);

        String message = isFavorite ? getActivity().getString(R.string.remove_from_favs) : getActivity().getString(R.string.added_to_favs);
        if (getView() != null)
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        isFavorite = !isFavorite;
        updateFab();
    }

    private void isFavoriteMovie() {
        //Check if it's favorite
        Cursor resCursor = getContext().getContentResolver().query(MoviesContract.MovieCategoryEntry.buildMovieCategoryData(), null,
                MoviesContract.MovieCategoryEntry.COLUMN_CATEGORY_ID + " =? AND " + MoviesContract.MovieCategoryEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{Integer.toString(Utils.getCategoryByName(getActivity(), getString(R.string.favorites_low))), Integer.toString(movieID)},
                null);

        isFavorite = resCursor != null && resCursor.moveToFirst();
        if (resCursor != null)
            resCursor.close();
    }

    public void updateFragment(int movieID){
        this.movieID = movieID;
        mAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(),initFragments(movieID));
        mViewpager.setAdapter(mAdapter);
    }
}
