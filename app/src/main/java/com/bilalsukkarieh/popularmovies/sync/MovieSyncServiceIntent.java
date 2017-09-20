package com.bilalsukkarieh.popularmovies.sync;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bilalsukkarieh.popularmovies.MainActivity;

public class MovieSyncServiceIntent extends IntentService {

    public MovieSyncServiceIntent() {
        super("MovieSyncServiceIntent");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String task = intent.getStringExtra("task");
        if(task.equals(MovieSyncUtils.TASK_MOVIES)){
            MovieSyncTasks.syncMovies(this, intent.getStringExtra("sort"));
        }else if(task.equals(MovieSyncUtils.TASK_TRAILER)){
            MovieSyncTasks.syncTrailers(this, intent.getStringExtra("movieId"));
        }else if(task.equals(MovieSyncUtils.TASK_REVIEW)){
            MovieSyncTasks.syncReviews(this, intent.getStringExtra("movieId"));
        }
    }
}
