package com.bilalsukkarieh.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MovieDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "popularmovie.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER  unique, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_DURATION + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_LOCAL_IMAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + " BOOLEAN NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SORT + " INTEGER NOT NULL " +
                ");";

        final String CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_THUMBNAIL + " TEXT NOT NULL " +

                ");";

        final String CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL " +
                ");";

        sqLiteDatabase.execSQL(CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
