package com.benavides.ramon.popularmovies.interfaces;

import com.benavides.ramon.popularmovies.data.Review;
import com.benavides.ramon.popularmovies.data.Trailer;

import java.util.ArrayList;

public interface DataTaskListener {
    void onDataRetrieved(boolean hasReceiveData);
}
