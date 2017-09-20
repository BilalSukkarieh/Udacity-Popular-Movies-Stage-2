package com.bilalsukkarieh.popularmovies;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bilalsukkarieh.popularmovies.data.MovieContract;
import com.bilalsukkarieh.popularmovies.sync.MovieSyncTasks;
import com.bilalsukkarieh.popularmovies.sync.MovieSyncUtils;
import com.squareup.picasso.Picasso;


import java.io.File;


public class MovieDetails extends AppCompatActivity implements TrailerAdapter.ItemClickListener,LoaderManager.LoaderCallbacks<Cursor>{

    //declare class variables
     String movieId;
     String thumbnailpath;
     String movieTitle;
     String movieRating;
     String movieProductionDate;
     String movieDuration;
     String movieOverview;
     ImageView img_thumbnail;
     TextView tv_title;
     TextView tv_production_date;
     TextView tv_rating;
     TextView tv_duration;
     TextView tv_overview;
     ProgressBar pb_movie;
     ScrollView ln_details;
     Boolean isFavorite;
     FloatingActionButton fab;
    final String OPERATION_ADD = "add";
    final String OPERATION_REMOVE = "remove";
    String sorttype;
    int sortvalue;
    String querySelection;
    RecyclerView rv_trailers;
    TrailerAdapter trailerAdapter;
    int trailerrecycpos = RecyclerView.NO_POSITION;
    ProgressBar pb_trailer;
    TextView tv_notrailers;
    Button bn_reviews;


    final int TRAILER_LOADER_ID = 88;


    public static final String[] TRAILER_PROJECTION = {
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_URL,
            MovieContract.TrailerEntry.COLUMN_TRAILER_THUMBNAIL

    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_TRAILER_URL = 1;
    public static final int INDEX_TRAILER_THUMBNAIL = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //reference required ui elements
        img_thumbnail = findViewById(R.id.img_thumbnail);
        tv_title = findViewById(R.id.tv_movie_title);
        tv_duration = findViewById(R.id.tv_duration);
        tv_rating = findViewById(R.id.tv_rating);
        tv_production_date = findViewById(R.id.tv_release_date);
        tv_overview = findViewById(R.id.tv_overview);
        pb_movie = findViewById(R.id.pbmovie);
        ln_details = findViewById(R.id.moviedetail);
        rv_trailers = findViewById(R.id.rv_trailers);
        pb_trailer = findViewById(R.id.pb_trialer);
        tv_notrailers = findViewById(R.id.tv_no_trailer);
        bn_reviews = findViewById(R.id.bn_reviews);

        //check for intent and get the attached data
        Intent detailIntent = getIntent();
        if(detailIntent != null){
            movieId = detailIntent.getStringExtra("movieId");
            thumbnailpath = detailIntent.getStringExtra("imgpath");
            movieTitle = detailIntent.getStringExtra("title");
            movieOverview = detailIntent.getStringExtra("description");
            movieProductionDate = detailIntent.getStringExtra("release");
            movieDuration = detailIntent.getStringExtra("duration");
            movieRating = detailIntent.getStringExtra("rating");
            String fav = detailIntent.getStringExtra("favorite");
            if(fav.equals("1")){
                isFavorite = true;
            }else if(fav.equals("0")){
                isFavorite = false;
            }
            sorttype = detailIntent.getStringExtra("sorttype");
            sortvalue = detailIntent.getIntExtra("sort", 0);
        }
        querySelection = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = " + "'" + movieId + "'";

        tv_title.setText(movieTitle);
        File f = new File(thumbnailpath);
        Picasso.with(this).load(f).into(img_thumbnail);
        tv_overview.setText(String.format(getString(R.string.overview), movieOverview));
        tv_production_date.setText(String.format(getString(R.string.releasedate), movieProductionDate));
        tv_rating.setText(String.format(getString(R.string.averagerating), movieRating));
        tv_duration.setText(String.format(getString(R.string.duration), movieDuration));

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(isFavorite){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_favorite));
        }else{
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite){
                    editFavorite(movieId, OPERATION_REMOVE, view);
                }else{
                    editFavorite(movieId, OPERATION_ADD, view);
                }

            }
        });

        setTrailerRecycler();
        loadingTrailer();
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);

        bn_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(MovieDetails.this, MovieReviews.class);
                reviewIntent.putExtra("movieId", movieId);
                reviewIntent.putExtra("movietitle", movieTitle);
                startActivity(reviewIntent);
            }
        });

    }

    public void editFavorite(String movieId, String operation, View view){

        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + "'" + Integer.parseInt(movieId) + "'";
        int i  = getContentResolver().update(
                MovieContract.MOVIE_CONTENT_URI,
                getMovieValues(operation),
                selection,
                null);
        if(i > 0){
            if(operation.equals(OPERATION_REMOVE)){
                Snackbar.make(view, getString(R.string.removedfav), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_favorite));
                isFavorite = false;
            }else if(operation.equals(OPERATION_ADD)){
                Snackbar.make(view, getString(R.string.addedtofav), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                isFavorite = true;
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_favorite));
            }
        }
    }

    public ContentValues getMovieValues(String operation){
        Boolean updateFav = false;
        if(operation.equals(OPERATION_ADD)){
            updateFav = true;
        }else if(operation.equals(OPERATION_REMOVE)){
            updateFav = false;
        }
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, Integer.parseInt(movieId));
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_LOCAL_IMAGE, thumbnailpath);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movieOverview);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DURATION, movieDuration);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE, movieProductionDate);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movieRating);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, updateFav);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE, sorttype);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_SORT, sortvalue);

        return cv;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case TRAILER_LOADER_ID:
                loadingTrailer();
                Uri teailerUri = MovieContract.TRAILER_CONTENT_URI;

                return new CursorLoader(this,
                        teailerUri,
                        TRAILER_PROJECTION,
                        querySelection,
                        null,
                        null);


            default:
                throw new RuntimeException("invalid loader: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == TRAILER_LOADER_ID){
            trailerAdapter.swapCursor(data);
            if (trailerrecycpos == RecyclerView.NO_POSITION){
                trailerrecycpos = 0;
            }
            if (data.getCount() != 0){
                showTrailers();
            }else{
                if(MovieSyncUtils.isInitialized){
                    MovieSyncUtils.startImmediateSync(this, null, MovieSyncUtils.TASK_TRAILER, movieId);
                }else{
                    MovieSyncUtils.initService(this, null, MovieSyncUtils.TASK_TRAILER, movieId);
                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == TRAILER_LOADER_ID){
            trailerAdapter.swapCursor(null);
        }
    }


    public void setTrailerRecycler(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MovieDetails.this, 2, LinearLayoutManager.VERTICAL, false);

        rv_trailers.setLayoutManager(gridLayoutManager);
        rv_trailers.setHasFixedSize(true);

        trailerAdapter = new TrailerAdapter(MovieDetails.this, MovieDetails.this);

        rv_trailers.setAdapter(trailerAdapter);
    }



    @Override
    public void onItemClick(Cursor itemCursor) {
        final String trailerURL = itemCursor.getString(INDEX_TRAILER_URL);
        final Dialog dialog = new Dialog(MovieDetails.this);
        dialog.setContentView(R.layout.dialog_trailer);
        dialog.setTitle(getString(R.string.reviewdialogtitle));
        dialog.setCancelable(false);

        Button sharebtn = (Button) dialog.findViewById(R.id.bn_share_trailer);
        Button playbtn = (Button) dialog.findViewById(R.id.bn_trailer_play);
        Button cancel = (Button) dialog.findViewById(R.id.bn_trailer_dialog_cancel);

        playbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent play = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerURL));
                startActivity(play);
            }
        });

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, trailerURL);
                startActivity(Intent.createChooser(share, getString(R.string.trailerdialogshare)));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showTrailers(){
        pb_trailer.setVisibility(View.GONE);
        rv_trailers.setVisibility(View.VISIBLE);
        tv_notrailers.setVisibility(View.GONE);
    }

    public void loadingTrailer(){
        pb_trailer.setVisibility(View.VISIBLE);
        rv_trailers.setVisibility(View.GONE);
        tv_notrailers.setVisibility(View.GONE);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            if (message.equals(MovieSyncTasks.NO_RESULT)) {
                if(intent.getStringExtra("type").equals(MovieSyncTasks.TRAILER_NO_RESULT)){
                    showNoTrailers();
                }
            }
        }
    };

    public void showNoTrailers(){
        tv_notrailers.setVisibility(View.VISIBLE);
        pb_trailer.setVisibility(View.GONE);
        rv_trailers.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MovieDetails.this).registerReceiver(
                mMessageReceiver, new IntentFilter("syncServiceNoResult"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(MovieDetails.this).unregisterReceiver(
                mMessageReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MovieDetails.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
