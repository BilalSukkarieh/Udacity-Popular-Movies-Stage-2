package com.bilalsukkarieh.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bilalsukkarieh.popularmovies.data.MovieContract;
import com.bilalsukkarieh.popularmovies.sync.MovieSyncTasks;
import com.bilalsukkarieh.popularmovies.sync.MovieSyncUtils;

public class MovieReviews extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    ProgressBar pb_review;
    TextView tv_noreviews;
    RecyclerView rv_reviews;
    ReviewAdapter reviewAdapter;
    String movieId;
    String querySelection;
    int reviewrecycpos = RecyclerView.NO_POSITION;
    String movieTitle;
    TextView tv_movie_title;

    public static final String[] REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT

    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_REVIEW_AUTHOR = 1;
    public static final int INDEX_REVIEW_CONTENT = 2;

    final int REVIEW_LOADER_ID = 87;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pb_review = findViewById(R.id.pb_reviews);
        tv_noreviews = findViewById(R.id.tv_noreviews);
        rv_reviews = findViewById(R.id.rv_reviews);
        tv_movie_title = findViewById(R.id.movie_review_title);

        Intent reviewIntent = getIntent();
        if(reviewIntent != null){
            movieId = reviewIntent.getStringExtra("movieId");
            movieTitle = reviewIntent.getStringExtra("movietitle");
        }

        tv_movie_title.setText(movieTitle);
        querySelection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = " + "'" + movieId + "'";

        setReviewRecycler();
        loadingReview();
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
    }

    public void setReviewRecycler(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieReviews.this, LinearLayoutManager.VERTICAL, false);

        rv_reviews.setLayoutManager(linearLayoutManager);
        rv_reviews.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter(MovieReviews.this);

        rv_reviews.setAdapter(reviewAdapter);
    }

    public void showReview(){
        pb_review.setVisibility(View.GONE);
        tv_noreviews.setVisibility(View.GONE);
        rv_reviews.setVisibility(View.VISIBLE);
    }

    public void loadingReview(){
        pb_review.setVisibility(View.VISIBLE);
        tv_noreviews.setVisibility(View.GONE);
        rv_reviews.setVisibility(View.GONE);
    }

    public void showNoReviews(){
        tv_noreviews.setVisibility(View.VISIBLE);
        pb_review.setVisibility(View.GONE);
        rv_reviews.setVisibility(View.GONE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){

            case REVIEW_LOADER_ID:
                loadingReview();
                Uri reviewUri = MovieContract.REVIEW_CONTENT_URI;

                return new CursorLoader(this,
                        reviewUri,
                        REVIEW_PROJECTION,
                        querySelection,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == REVIEW_LOADER_ID){
            reviewAdapter.swapCursor(data);
            if (reviewrecycpos == RecyclerView.NO_POSITION){
                reviewrecycpos = 0;
            }
            if (data.getCount() != 0){
                showReview();
            }else{
                if(MovieSyncUtils.isInitialized){
                    MovieSyncUtils.startImmediateSync(this, null, MovieSyncUtils.TASK_REVIEW, movieId);
                }else{
                    MovieSyncUtils.initService(this, null, MovieSyncUtils.TASK_REVIEW, movieId);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == REVIEW_LOADER_ID){
            reviewAdapter.swapCursor(null);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            if (message.equals(MovieSyncTasks.NO_RESULT)) {
                if (intent.getStringExtra("type").equals(MovieSyncTasks.REVIEW_NO_RESULT)){
                    showNoReviews();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MovieReviews.this).registerReceiver(
                mMessageReceiver, new IntentFilter("syncServiceNoResult"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(MovieReviews.this).unregisterReceiver(
                mMessageReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MovieReviews.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
