package com.benavides.ramon.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.benavides.ramon.popularmovies.fragments.ActorInfoFragment;
import com.benavides.ramon.popularmovies.fragments.MovieDetailContainerFragment;
import com.benavides.ramon.popularmovies.interfaces.ActorSelectorListener;
import com.benavides.ramon.popularmovies.interfaces.DetailContentChangeListener;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorListener;
import com.benavides.ramon.popularmovies.sync.PopularmoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MovieSelectorListener, DetailContentChangeListener, ActorSelectorListener {

    private static final String DETAIL_FRG_TAG = "detail_frg";
    private static final String ACTOR_FRG_TAG = "actor_frg";
    private boolean twoPane;
    private int mMovieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PopularmoviesSyncAdapter.initSyncAdapter(this);
        twoPane = findViewById(R.id.detail_frg_container) != null;

        if (savedInstanceState != null && twoPane) {
            mMovieID = savedInstanceState.getInt(getString(R.string.movie_intent_tag));
            if (mMovieID != 0){
                MovieDetailContainerFragment movieDetailFragment = MovieDetailContainerFragment.newInstance(mMovieID);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_frg_container, movieDetailFragment, DETAIL_FRG_TAG)
                        .commit();
            }
        }
    }

    @Override
    public void onMovieSelected(int movie) {

        mMovieID = movie;
        Fragment frg = getSupportFragmentManager().findFragmentByTag(DETAIL_FRG_TAG);

        if (twoPane && frg == null) {
            MovieDetailContainerFragment movieDetailFragment = MovieDetailContainerFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_frg_container, movieDetailFragment, DETAIL_FRG_TAG)
                    .commit();

        } else if (twoPane && frg != null) {
            ((MovieDetailContainerFragment) frg).updateFragment(movie);
            //pop last actor fragment if exists
            if (getSupportFragmentManager().findFragmentByTag(ACTOR_FRG_TAG) != null)
                getSupportFragmentManager().popBackStack();
        } else {  //If it's mobile or tablet at portrait
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(getString(R.string.movie_intent_tag), movie);
            detailIntent.putExtra(getString(R.string.two_pane_param), twoPane);
            startActivity(detailIntent);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.movie_intent_tag), mMovieID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onContentChanged(String title, String backdropURL) {
        MovieDetailContainerFragment frg = ((MovieDetailContainerFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRG_TAG));
        if (frg != null) {
            frg.updateHeader(title, backdropURL);
        }
    }

    //actor selection callback
    @Override
    public void onActorSelected(int actorId) {

        if (twoPane) {
            ActorInfoFragment frg = ActorInfoFragment.newInstance(actorId, twoPane);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_frg_container, frg, ACTOR_FRG_TAG).addToBackStack(ACTOR_FRG_TAG)
                    .commit();
        } else {
            Intent actorInfoIntent = new Intent(this, ActorInfoActivity.class);
            actorInfoIntent.putExtra(getString(R.string.actor_id_param), actorId);
            startActivity(actorInfoIntent);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        super.onSaveInstanceState(outState, outPersistentState);
    }
}
