package com.bilalsukkarieh.popularmovies.sync;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bilalsukkarieh.popularmovies.MainActivity;
import com.bilalsukkarieh.popularmovies.data.MovieContract;

public class MovieSyncUtils {

    public static boolean isInitialized;
    final public static String TASK_MOVIES = "movies";
    final public static String TASK_TRAILER = "trailers";
    final public static String TASK_REVIEW = "reviews";

    synchronized public static void initService(final Context context, String sort, String task, String movieId){
        if (isInitialized) return;

        isInitialized = true;
        startImmediateSync(context, sort, task, movieId);

    }

    public static void startImmediateSync(@NonNull final Context context, String sort, String task, String movieId) {
        Intent syncintent = new Intent(context, MovieSyncServiceIntent.class);
        syncintent.putExtra("task", task);
        syncintent.putExtra("sort", sort);
        syncintent.putExtra("movieId", movieId);
        context.startService(syncintent);
    }
}
