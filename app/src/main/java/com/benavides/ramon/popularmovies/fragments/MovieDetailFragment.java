package com.benavides.ramon.popularmovies.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    @Nullable
    @BindView(R.id.message_info_container)
    RelativeLayout messageInfoContainer;

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

        //Managing device type and orientation
        if (!Utils.isTablet(getActivity()) || (Utils.isTablet(getActivity()) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        } else {//Setting invisible layout items to show message in case the device is a tablet with landscape orientation
            setVisibilityLayoutItems(false);
        }

//        Managing toolbar back button behavior
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        //Getting movie object from intent to display info
        Intent mainActivityIntent = getActivity().getIntent();
        if (mainActivityIntent != null && mainActivityIntent.hasExtra(getActivity().getString(R.string.movie_intent_tag))) {
            Movie movie = (Movie) mainActivityIntent.getParcelableExtra(getActivity().getString(R.string.movie_intent_tag));
            updateContent(movie);
        }


    }

    //    Managing configuration changes to show or not toolbar navigation button
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(!(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && Utils.isTablet(getActivity())));
    }

    //Populate content
    public void updateContent(Movie movie) {
        collapsingToolbarLayout.setTitle(movie.getOriginalTitle());

        Picasso.with(getActivity())
                .load(movie.getBackdrop())
                /*.placeholder(R.drawable.sad)*/
                /*.error(R.drawable.exclamation)*/
                .into(backdropImv);

        synopsisTev.setText(movie.getSynopsis());
        userRatingTev.setText(NumberFormat.getNumberInstance().format(movie.getRating()));
        releaseDateTev.setText(movie.getReleaseDate());
    }

    public void setVisibilityLayoutItems(boolean visible) {
        if (visible) {
            appBarLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            if(messageInfoContainer!=null)
                messageInfoContainer.setVisibility(View.GONE);
        } else {
            appBarLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            if(messageInfoContainer!=null)
                messageInfoContainer.setVisibility(View.VISIBLE);
        }

    }
}
