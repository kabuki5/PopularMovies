package com.benavides.ramon.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.benavides.ramon.popularmovies.fragments.MovieDetailContainerFragment;
import com.benavides.ramon.popularmovies.interfaces.DetailContentChangeListener;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorListener;
import com.benavides.ramon.popularmovies.sync.PopularmoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MovieSelectorListener, DetailContentChangeListener {

    private static final String DETAIL_FRG_TAG = "detail_frg";
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PopularmoviesSyncAdapter.initSyncAdapter(this);
    }

    @Override
    public void onMovieSelected(int movie) {

        //If it's tablet at landscape orientation
        twoPane = findViewById(R.id.detail_frg_container) != null;


        //TODO => rethink this!!!!
        if(twoPane){
            MovieDetailContainerFragment movieDetailFragment = MovieDetailContainerFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_frg_container, movieDetailFragment, DETAIL_FRG_TAG)
                    .commit();

        }else{  //If it's mobile or tablet at portrait
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(getString(R.string.movie_intent_tag), movie);
            startActivity(detailIntent);
        }
    }


    @Override
    public void onContentChanged(String title, String backdropURL) {
        MovieDetailContainerFragment frg = ((MovieDetailContainerFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRG_TAG));
        if(frg != null){
            frg.updateHeader(title,backdropURL);
        }
    }
}
