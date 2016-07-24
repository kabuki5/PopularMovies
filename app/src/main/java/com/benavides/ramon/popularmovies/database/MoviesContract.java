package com.benavides.ramon.popularmovies.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ramon on 21/7/16.
 */
public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.benavides.ramon.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_RATING = "user_rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_BACKDROP = "backdrop";

        public static Uri buildMoviesData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String [] MOVIES_PROJECTION = {
                _ID,
                COLUMN_TITLE,
                COLUMN_SYNOPSIS,
                COLUMN_POSTER,
                COLUMN_RATING,
                COLUMN_RELEASE_DATE,
                COLUMN_BACKDROP
        };

// These are tied with the MOVIES_PROJECTION
        public static final int MOVIES_COLUMN_ID = 0;
        public static final int MOVIES_COLUMN_TITLE = 1;
        public static final int MOVIES_COLUMN_SYNOPSIS = 2;
        public static final int MOVIES_COLUMN_POSTER = 3;
        public static final int MOVIES_COLUMN_RATING = 4;
        public static final int MOVIES_COLUMN_RELEASE_DATE = 5;
        public static final int MOVIES_COLUMN_BACKDROP = 6;

    }


}

