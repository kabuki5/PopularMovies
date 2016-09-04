package com.benavides.ramon.popularmovies.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 */
public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.benavides.ramon.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_MOVIE_CATEGORY = "movie_category";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_CAST = "cast";
    public static final String PATH_ACTORS = "actors";


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

        public static final String COLUMN_ORDER = "tmp_order";

        public static Uri buildMoviesData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildMoviesDataWithCategory(int category) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(category)).build();
        }

        public static int getCategoryFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] MOVIES_PROJECTION = {
                _ID,
                COLUMN_TITLE,
                COLUMN_SYNOPSIS,
                COLUMN_POSTER,
                COLUMN_RATING,
                COLUMN_RELEASE_DATE,
                COLUMN_BACKDROP,
                COLUMN_ORDER
        };

        // These are tied with the MOVIES_PROJECTION
        public static final int MOVIES_COLUMN_ID = 0;
        public static final int MOVIES_COLUMN_TITLE = 1;
        public static final int MOVIES_COLUMN_SYNOPSIS = 2;
        public static final int MOVIES_COLUMN_POSTER = 3;
        public static final int MOVIES_COLUMN_RATING = 4;
        public static final int MOVIES_COLUMN_RELEASE_DATE = 5;
        public static final int MOVIES_COLUMN_BACKDROP = 6;
        public static final int MOVIES_COLUMN_ORDER = 7;
    }


    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();

        public static final String TABLE_NAME = "categories";

        public static final String COLUMN_NAME = "name";

        public static final String[] CATEGORIES_PROJECTION = {
                _ID,
                COLUMN_NAME
        };

        public static final int CATEGORIES_COLUMN_ID = 0;
        public static final int CATEGORIES_COLUMN_NAME = 1;

        public static Uri buildCategoryData() {
            return CONTENT_URI.buildUpon().build();
        }


        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MovieCategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_CATEGORY).build();

        public static final String TABLE_NAME = "movie_category";

        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static final String[] CATEGORY_MOVIE_PROJECTION = {
                _ID,
                COLUMN_CATEGORY_ID,
                COLUMN_MOVIE_ID
        };

        public static Uri buildMovieCategoryData() {
            return CONTENT_URI.buildUpon().build();
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildReviewData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static final String[] REVIEW_PROJECTION = {
                _ID,
                COLUMN_AUTHOR,
                COLUMN_CONTENT,
                COLUMN_MOVIE_ID
        };

        // These are tied with the REVIEW_PROJECTION
        public static final int REVIEWS_COLUMN_ID = 0;
        public static final int REVIEWS_COLUMN_AUTHOR = 1;
        public static final int REVIEWS_COLUMN_CONTENT = 2;
        public static final int REVIEWS_COLUMN_MOVIE_ID = 3;
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildTrailerData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static final String[] TRAILER_PROJECTION = {
                _ID,
                COLUMN_NAME,
                COLUMN_SOURCE,
                COLUMN_MOVIE_ID
        };

        // These are tied with the TRAILER_PROJECTION
        public static final int TRAILER_COLUMN_ID = 0;
        public static final int TRAILER_COLUMN_NAME = 1;
        public static final int TRAILER_COLUMN_SOURCE = 2;
        public static final int TRAILER_COLUMN_MOVIE_ID = 3;
    }

    public static final class CastEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAST).build();

        public static final String TABLE_NAME = "cast";

        public static final String COLUMN_CHARACTER = "character";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PICTURE = "picture";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildCastData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static final String[] CAST_PROJECTION = {
                _ID,
                COLUMN_CHARACTER,
                COLUMN_NAME,
                COLUMN_PICTURE,
                COLUMN_MOVIE_ID
        };

        // These are tied with the CAST_PROJECTION
        public static final int CAST_COLUMN_ID = 0;
        public static final int CAST_COLUMN_CHARACTER = 1;
        public static final int CAST_COLUMN_NAME = 2;
        public static final int CAST_COLUMN_PICTURE = 3;
        public static final int CAST_COLUMN_MOVIE_ID = 4;
    }

    public static final class ActorsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTORS).build();

        public static final String TABLE_NAME = "actors";

        public static final String COLUMN_BIOGRAPHY = "biography";
        public static final String COLUMN_BIRTHDAY = "birthday";
        public static final String COLUMN_DEATHDAY = "deathday";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PLACE = "place";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_PICTURE = "picture";


        public static Uri buildActorsData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static final String[] ACTORS_PROJECTION = {
                _ID,
                COLUMN_BIOGRAPHY,
                COLUMN_BIRTHDAY,
                COLUMN_DEATHDAY,
                COLUMN_NAME,
                COLUMN_PLACE,
                COLUMN_POPULARITY,
                COLUMN_PICTURE
        };

        // These are tied with the ACTORS_PROJECTION
        public static final int ACTORS_COLUMN_ID = 0;
        public static final int ACTORS_COLUMN_BIOGRAPHY = 1;
        public static final int ACTORS_COLUMN_BIRTHDAY = 2;
        public static final int ACTORS_COLUMN_DEATHDAY = 3;
        public static final int ACTORS_COLUMN_NAME = 4;
        public static final int ACTORS_COLUMN_PLACE = 5;
        public static final int ACTORS_COLUMN_POPULARITY = 6;
        public static final int ACTORS_COLUMN_PICTURE = 7;
    }

}

