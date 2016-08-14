package com.benavides.ramon.popularmovies.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benavides.ramon.popularmovies.MainActivity;
import com.benavides.ramon.popularmovies.MovieDetailActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ramon on 16/7/16.
 */
public class MovieDetailFragment extends Fragment {

    private static final String MOVIE_PARAM = "movie";
    private static final String TWO_PANE = "two_pane";

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.backdrop)
    ImageView backdropImv;
    @BindView(R.id.synopsis_tev)
    TextView synopsisTev;
    @BindView(R.id.user_rating_tev)
    TextView userRatingTev;
    @BindView(R.id.release_date_tev)
    TextView releaseDateTev;
    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    private Movie mMovie;

    public static MovieDetailFragment newInstance(Movie movie, boolean twoPane) {

        Bundle args = new Bundle();
        args.putParcelable(MOVIE_PARAM, movie);
        args.putBoolean(TWO_PANE, twoPane);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setting up toolbar
        Toolbar toolbar = ButterKnife.findById(view, R.id.toolbar);


        //Managing action bar navigation back button state
        if(getActivity() instanceof MovieDetailActivity){
            ActionBar actionBar = ((MovieDetailActivity)getActivity()).getSupportActionBar();
            if(actionBar == null)
                ((MovieDetailActivity)getActivity()).setSupportActionBar(toolbar);
            actionBar = ((MovieDetailActivity)getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }else{
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar == null)
                ((MovieDetailActivity)getActivity()).setSupportActionBar(toolbar);
            actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);

        }

        //        Managing toolbar back button behavior
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


//        Restoring instance state
        if(savedInstanceState != null){
            mMovie = savedInstanceState.getParcelable(MOVIE_PARAM);
            updateContent(mMovie);
        }else{
            //Getting mMovie object from intent or arguments bundle to display info
            Bundle bundle = getArguments();
            if(bundle!=null && bundle.containsKey(MOVIE_PARAM)){
                mMovie = bundle.getParcelable(MOVIE_PARAM);
                updateContent(mMovie);
            }else if(getActivity().getIntent() != null && getActivity().getIntent().hasExtra(getActivity().getString(R.string.movie_intent_tag))) {
                mMovie = (Movie) getActivity().getIntent().getParcelableExtra(getActivity().getString(R.string.movie_intent_tag));
                updateContent(mMovie);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_PARAM, mMovie);
        super.onSaveInstanceState(outState);
    }


    //    Managing configuration changes to show or not toolbar navigation button
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    //Populate content
    public void updateContent(Movie movie) {
        mMovie = movie;
        collapsingToolbarLayout.setTitle(movie.getOriginalTitle());

        Picasso.with(getActivity())
                .load(movie.getBackdrop())
                /*.placeholder(R.drawable.ic_movie)*/
                /*.error(R.drawable.exclamation)*/
                .into(backdropImv);

        synopsisTev.setText(movie.getSynopsis());
        userRatingTev.setText(NumberFormat.getNumberInstance().format(movie.getRating()));
        releaseDateTev.setText(movie.getReleaseDate());
    }

}
