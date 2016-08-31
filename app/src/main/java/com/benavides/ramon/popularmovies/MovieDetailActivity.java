package com.benavides.ramon.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.benavides.ramon.popularmovies.fragments.MovieDetailContainerFragment;
import com.benavides.ramon.popularmovies.interfaces.DetailContentChangeListener;

/**
 */
public class MovieDetailActivity extends AppCompatActivity implements DetailContentChangeListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);

    }

    @Override
    public void onContentChanged(String title, String backdropURL) {
        MovieDetailContainerFragment frg = ((MovieDetailContainerFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_frg));
        if(frg != null){
            frg.updateHeader(title,backdropURL);
        }
    }
}
