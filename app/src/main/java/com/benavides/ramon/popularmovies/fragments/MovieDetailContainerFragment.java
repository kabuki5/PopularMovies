package com.benavides.ramon.popularmovies.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Contain CoordinatorLayout with viewpager
 */
public class MovieDetailContainerFragment extends Fragment {

    private static final String MOVIE_PARAM = "movie";
    private static final int MAX_FRGS = 3;
    private static final int FRG_DETAIL = 0;
    private static final int FRG_REVIEWS = 1;
    private static final int FRG_TRAILERS = 2;

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


    public static MovieDetailContainerFragment newInstance(int movie) {

        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movie);
        MovieDetailContainerFragment fragment = new MovieDetailContainerFragment();
        fragment.setArguments(args);
        return fragment;
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

        int movieID;
        if (getArguments() != null)
            movieID = getArguments().getInt(MOVIE_PARAM);
        else {// from intent
            movieID = getActivity().getIntent().getExtras().getInt(getString(R.string.movie_intent_tag));
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
        } else {
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

        SectionsPagerAdapter mAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), initFragments(movieID));

        mViewpager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewpager);

        setupTabs();
    }

    private void setupTabs() {
        TypedArray images = getResources().obtainTypedArray(R.array.tab_icons);
        String[] titles = getResources().getStringArray(R.array.tab_titles);

           for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_tab, null);
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                ((ImageView) view.findViewById(R.id.tab_view_img)).setImageResource(images.getResourceId(i, R.drawable.ic_movie));
                ((TextView) view.findViewById(R.id.tab_view_txt)).setText(titles[i]);
                tab.setCustomView(view);
            }
        }
    }

    private Fragment[] initFragments(int movieID) {
        Fragment[] mFragments = new Fragment[MAX_FRGS];
        mFragments[FRG_DETAIL] = MovieSynopsisFragment.newInstance(movieID);
        mFragments[FRG_REVIEWS] = ReviewsFragment.newInstance(movieID);
        mFragments[FRG_TRAILERS] = TrailersFragment.newInstance(movieID);

        return mFragments;
    }

    public void updateContent(String title, String backdropURL) {
        collapsingToolbarLayout.setTitle(title);

        Picasso.with(getActivity())
                .load(backdropURL)
                //.placeholder(R.drawable.ic_movie)
                //.error(R.drawable.exclamation)
                .into(backdropImv);
    }


}
