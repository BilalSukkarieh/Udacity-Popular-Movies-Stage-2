package com.bilalsukkarieh.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class MovieProvider extends ContentProvider {

    public static final int MOVIE_CODE = 100;
    public static final int TRAILER_CODE = 200;
    public static final int REVIEW_CODE = 300;
    public static final UriMatcher uriMatcher = uriMatcher();
    private MovieDbHelper movieDbHelper;

    public static UriMatcher uriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIE_PATH, MOVIE_CODE);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.TRAILER_PATH, TRAILER_CODE);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.REVIEW_PATH, REVIEW_CODE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor queryCursor;
        switch (uriMatcher.match(uri)){
            case MOVIE_CODE:
                queryCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRAILER_CODE:
                queryCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW_CODE:
                queryCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return queryCursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case MOVIE_CODE:
                return insertCaseBulk(db, MovieContract.MovieEntry.TABLE_NAME, values, uri);

            case TRAILER_CODE:
                return insertCaseBulk(db, MovieContract.TrailerEntry.TABLE_NAME, values, uri);

            case REVIEW_CODE:
                return insertCaseBulk(db, MovieContract.ReviewEntry.TABLE_NAME, values, uri);

            default:
                return super.bulkInsert(uri, values);
        }



    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numDeleted;
        switch (uriMatcher.match(uri)){
            case MOVIE_CODE:
                numDeleted = movieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                return numDeleted;
            case TRAILER_CODE:
                numDeleted = movieDbHelper.getWritableDatabase().delete(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                return numDeleted;
            case REVIEW_CODE:
                numDeleted = movieDbHelper.getWritableDatabase().delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                return numDeleted;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selcargs) {
        int numupdated;
        switch (uriMatcher.match(uri)){
            case MOVIE_CODE:
                numupdated = movieDbHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selcargs);
                return numupdated;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    int insertCaseBulk(SQLiteDatabase db, String tablename, ContentValues[] values, Uri uri){
        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tablename, null, value);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
    }
}
