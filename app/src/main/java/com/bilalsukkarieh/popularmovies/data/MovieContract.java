package com.bilalsukkarieh.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    public static final String AUTHORITY = "com.bilalsukkarieh.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String MOVIE_PATH = "movies";
    public static final String TRAILER_PATH = "trailers";
    public static final String REVIEW_PATH = "reviews";


    private MovieContract(){}

    public class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "mid";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_DESCRIPTION = "desc";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE = "release";
        public static final String COLUMN_MOVIE_DURATION = "duration";
        public static final String COLUMN_MOVIE_LOCAL_IMAGE = "localimg";
        public static final String COLUMN_MOVIE_FAVORITE = "isfavorite";
        public static final String COLUMN_MOVIE_SORT_TYPE = "type";
        public static final String COLUMN_MOVIE_SORT = "sort";
    }

    public class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_MOVIE_ID = "mid";
        public static final String COLUMN_TRAILER_URL = "url";
        public static final String COLUMN_TRAILER_THUMBNAIL = "thumbnail";
    }

    public class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "mid";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "review";
    }

    public static final Uri MOVIE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();
    public static final Uri TRAILER_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TRAILER_PATH).build();
    public static final Uri REVIEW_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(REVIEW_PATH).build();

}
