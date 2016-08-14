package com.benavides.ramon.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.benavides.ramon.popularmovies.data.Movie;

import butterknife.ButterKnife;

/**
 * Created by ramon on 16/7/16.
 */
public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);
    }
}
