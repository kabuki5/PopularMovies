package com.benavides.ramon.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.benavides.ramon.popularmovies.fragments.MovieDetailContainerFragment;
import com.benavides.ramon.popularmovies.interfaces.ActorSelectorListener;
import com.benavides.ramon.popularmovies.interfaces.DetailContentChangeListener;

/**
 */
public class MovieDetailActivity extends AppCompatActivity implements DetailContentChangeListener, ActorSelectorListener {

    private boolean mTwoPane;
    private int mMovieId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);

        mMovieId = getIntent().getExtras().getInt(getString(R.string.movie_intent_tag));
        mTwoPane = getIntent().getExtras().getBoolean(getString(R.string.two_pane_param));
    }

    @Override
    public void onContentChanged(String title, String backdropURL) {
        MovieDetailContainerFragment frg = ((MovieDetailContainerFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_frg));
        if(frg != null){
            frg.updateHeader(title,backdropURL);
        }
    }

    @Override
    public void onActorSelected(int actorId) {
        Intent actorInfoIntent = new Intent(this, ActorInfoActivity.class);
        actorInfoIntent.putExtra(getString(R.string.actor_id_param), actorId);
        startActivity(actorInfoIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mTwoPane && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Intent returnIntent = new Intent();
            returnIntent.putExtra(getString(R.string.movie_intent_tag),mMovieId);
            finish();
        }
    }
}
