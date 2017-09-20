package com.bilalsukkarieh.popularmovies.sync;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bilalsukkarieh.popularmovies.R;
import com.bilalsukkarieh.popularmovies.data.MovieContract;
import com.bilalsukkarieh.popularmovies.utils.HttpUtils;
import com.bilalsukkarieh.popularmovies.utils.JsonUtils;
import com.bilalsukkarieh.popularmovies.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieSyncTasks {

    final public static String NO_RESULT = "noresult";
    final public static String TRAILER_NO_RESULT = "trailer";
    final public static String REVIEW_NO_RESULT = "review";

    public static synchronized void syncMovies(Context context, String sort){

        try {
            Log.i("service", "ok");
            ContentValues[] movieCV;
            ArrayList<HashMap<String,String>> movieData;
            String url = "http://api.themoviedb.org/3/movie/"+sort+"?api_key=" + context.getString(R.string.moviedbapi);
            HttpUtils httpUtils = new HttpUtils();
            String jsonStr = httpUtils.makeServiceCall(url);
            movieData = JsonUtils.extractMovieData(context, jsonStr);
            int position = 0;
            int sortvalue = 0;
            movieCV = new ContentValues[movieData.size()];
            for(HashMap<String, String> m: movieData){
                Log.i("jsonresult", m.toString());
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, m.get("id"));
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, m.get("movietitle"));
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, m.get("moviedesc"));
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, m.get("movierating"));
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE, m.get("movierelease"));
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DURATION, Integer.parseInt(m.get("moviedur")));
                Bitmap bitmap = Utils.DownloadImage(m.get("imageURL"));
                String imglocation = JsonUtils.saveImgLocally(context, bitmap, m.get("id"));
                Log.i("savedimgz", " img url: "+ imglocation);
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_LOCAL_IMAGE, imglocation);
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, false);
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE, sort);
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_SORT, sortvalue);

                movieCV[position] = cv;
                position++;
                sortvalue++;
            }
            if(movieCV != null && movieCV.length != 0){
                String deleteSelec = MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE + " = " + "'" + sort + "'";
                context.getContentResolver().delete(MovieContract.MOVIE_CONTENT_URI,deleteSelec,null);
                context.getContentResolver().bulkInsert(MovieContract.MOVIE_CONTENT_URI, movieCV);
            }

        }catch (Exception e){
            Log.i("syncMovie", e.toString());
        }
    }

    public static synchronized void syncTrailers(Context context, String movieId){
        try {
            Log.i("service", "trailer ok");
            ContentValues[] trailerCV;
            ArrayList<HashMap<String,String>> trailerData;
            String url = "http://api.themoviedb.org/3/movie/"+movieId+"/videos?api_key=" + context.getString(R.string.moviedbapi);
            HttpUtils httpUtils = new HttpUtils();
            String jsonStr = httpUtils.makeServiceCall(url);
            trailerData = JsonUtils.extractTrailerData(context, jsonStr);
            trailerCV = new ContentValues[trailerData.size()];
            int position = 0;
            for(HashMap<String, String> t: trailerData){
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_URL, t.get("trailerURL"));
                Bitmap bitmap = Utils.DownloadImage(JsonUtils.getYoutubeThumbnailURL(context, t.get("trailerKEY")));
                String trailerimgpath = JsonUtils.saveImgLocally(context, bitmap, "trailer_" + position + "_" + movieId);
                cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_THUMBNAIL, trailerimgpath);
                trailerCV[position] = cv;
                position++;
            }
            if(trailerCV != null && trailerCV.length != 0){
                String deleteSelec = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = " + "'" + movieId + "'";
                context.getContentResolver().delete(MovieContract.TRAILER_CONTENT_URI,deleteSelec,null);
                context.getContentResolver().bulkInsert(MovieContract.TRAILER_CONTENT_URI, trailerCV);
            }else{
                returnNoResults(context, NO_RESULT, TRAILER_NO_RESULT);
            }

        }catch (Exception e){
            Log.i("syncMovie", e.toString());
        }
    }

    public static synchronized void syncReviews(Context context, String movieId){
        try {
            Log.i("reviews", "Review ok");
            ContentValues[] reviewCV;
            ArrayList<HashMap<String,String>> reviewData;
            String url = "http://api.themoviedb.org/3/movie/"+movieId+"/reviews?api_key=" + context.getString(R.string.moviedbapi);
            HttpUtils httpUtils = new HttpUtils();
            String jsonStr = httpUtils.makeServiceCall(url);
            reviewData = JsonUtils.extractReviewData(context, jsonStr);
            reviewCV = new ContentValues[reviewData.size()];
            int position = 0;
            for(HashMap<String, String> r: reviewData){
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, r.get("author"));
                cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, r.get("content"));
                reviewCV[position] = cv;
                position++;
            }
            if(reviewCV != null && reviewCV.length != 0){
                String deleteSelec = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = " + "'" + movieId + "'";
                context.getContentResolver().delete(MovieContract.REVIEW_CONTENT_URI,deleteSelec,null);
                context.getContentResolver().bulkInsert(MovieContract.REVIEW_CONTENT_URI, reviewCV);
            }else{
                returnNoResults(context, NO_RESULT, REVIEW_NO_RESULT);
            }

        }catch (Exception e){
            Log.i("syncMovie", e.toString());
        }
    }

    private static void returnNoResults(Context context, String msg, String syncType) {
        Intent intent = new Intent("syncServiceNoResult");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        intent.putExtra("type", syncType);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
