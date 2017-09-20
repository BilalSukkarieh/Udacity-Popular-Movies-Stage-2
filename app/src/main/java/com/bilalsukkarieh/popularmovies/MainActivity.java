package com.bilalsukkarieh.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bilalsukkarieh.popularmovies.data.MovieContract;
import com.bilalsukkarieh.popularmovies.data.MoviePreferences;
import com.bilalsukkarieh.popularmovies.sync.MovieSyncUtils;



public class MainActivity extends AppCompatActivity implements  MovieAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    //declaring class variables
    MovieAdapter movieAdapter;
    RecyclerView rv_movie_grid;
    public static String movieSort;
    ProgressBar pb;
    private int recyclerPosition = RecyclerView.NO_POSITION;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    String querySelection;
    TextView tv_nofav;
    GridLayoutManager gridLayoutManager;
    Parcelable recyclerState;

    final static int MOVIE_LOADER_ID = 89;

    public static final String[] MAINACTIVITY_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_LOCAL_IMAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE,
            MovieContract.MovieEntry.COLUMN_MOVIE_DURATION,
            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE,
            MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE,
            MovieContract.MovieEntry.COLUMN_MOVIE_SORT

    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_IMAGE = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    public static final int INDEX_MOVIE_DESCRIPTION = 3;
    public static final int INDEX_MOVIE_RELEASE = 4;
    public static final int INDEX_MOVIE_DURATION = 5;
    public static final int INDEX_MOVIE_RATING = 6;
    public static final int INDEX_MOVIE_FAVORITE = 7;
    public static final int INDEX_MOVIE_SORT_TYPE = 8;
    public static final int INDEX_MOVIE_SORT = 9;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencing required ui elements
        rv_movie_grid = findViewById(R.id.rv_movie_grid);
        pb = findViewById(R.id.pb_load_images);
        tv_nofav = findViewById(R.id.tv_no_fav);



        movieSort = MoviePreferences.getPreferedSort(this);
        querySelection = MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE + " = " + "'" + movieSort + "'";

        if(movieSort.equals(MoviePreferences.POPULAR_SORT_PREFERENCE)){
            //setting activity label to most popular as it is default sort
            getSupportActionBar().setTitle(getString(R.string.mostpopular));
        }else if(movieSort.equals(MoviePreferences.TOP_SORT_PREFERENCE)){
            //setting activity label to most popular as it is default sort
            getSupportActionBar().setTitle(getString(R.string.toprated));
        }else if(movieSort.equals(MoviePreferences.FAVORITE_PREFERENCE)){
            getSupportActionBar().setTitle(getString(R.string.favtitle));
        }

        setRecycler();

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if(key.equals(MoviePreferences.SORT_PREFERENCE_KEY)){
                    if(!prefs.getString(MoviePreferences.SORT_PREFERENCE_KEY, MoviePreferences.POPULAR_SORT_PREFERENCE).equals(MoviePreferences.FAVORITE_PREFERENCE)){
                        movieSort = MoviePreferences.getPreferedSort(MainActivity.this);
                        querySelection = MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE + " = " + "'" + movieSort + "'";
                        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    }else{
                        getFavorites();
                    }
                }
            }
        };

        showLoading();

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(prefListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the sort menu to allow user to change sort
        getMenuInflater().inflate(R.menu.sortmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort_popular){
            MoviePreferences.setPreferedSort(MainActivity.this, MoviePreferences.POPULAR_SORT_PREFERENCE);
            showLoading();
            //change the label of activity to mach the sort chosen by user
            getSupportActionBar().setTitle(getString(R.string.mostpopular));
        }
        if(item.getItemId() == R.id.action_sort_top){
            MoviePreferences.setPreferedSort(MainActivity.this, MoviePreferences.TOP_SORT_PREFERENCE);
            showLoading();
            //change the label of activity to mach the sort chosen by user
            getSupportActionBar().setTitle(getString(R.string.toprated));
        }
        if(item.getItemId() == R.id.action_favorites){
            MoviePreferences.setPreferedSort(MainActivity.this, MoviePreferences.FAVORITE_PREFERENCE);
            getFavorites();
        }
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case MOVIE_LOADER_ID:
                Uri moviesUri = MovieContract.MOVIE_CONTENT_URI;
                String sortOrder = MovieContract.MovieEntry.COLUMN_MOVIE_SORT + " ASC";
                //String selection = MovieContract.MovieEntry.COLUMN_MOVIE_SORT_TYPE + " = " + "'" + movieSort + "'";

                return new CursorLoader(this,
                        moviesUri,
                        MAINACTIVITY_MOVIE_PROJECTION,
                        querySelection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("invalid loader: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        if (recyclerPosition == RecyclerView.NO_POSITION){
            recyclerPosition = 0;
        }
        rv_movie_grid.smoothScrollToPosition(recyclerPosition);
        if(querySelection.equals(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + " = 1")){
            if (data.getCount() != 0){
                showData();
            }else{
                showNoFav();
            }

        }else{
            if (data.getCount() != 0){
                showData();
            }else{
                if(MovieSyncUtils.isInitialized){
                    MovieSyncUtils.startImmediateSync(this, movieSort, MovieSyncUtils.TASK_MOVIES, null);
                }else{
                    MovieSyncUtils.initService(this, movieSort, MovieSyncUtils.TASK_MOVIES, null);
                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }


    private void showLoading(){
        pb.setVisibility(View.VISIBLE);
        rv_movie_grid.setVisibility(View.GONE);
        tv_nofav.setVisibility(View.GONE);

    }

    private void showData(){
        pb.setVisibility(View.GONE);
        rv_movie_grid.setVisibility(View.VISIBLE);
        tv_nofav.setVisibility(View.GONE);
    }

    public void showNoFav(){
        pb.setVisibility(View.GONE);
        rv_movie_grid.setVisibility(View.GONE);
        tv_nofav.setVisibility(View.VISIBLE);
    }

    public void setRecycler(){
        gridLayoutManager = new GridLayoutManager(MainActivity.this, 2, LinearLayoutManager.VERTICAL, false);

        rv_movie_grid.setLayoutManager(gridLayoutManager);
        rv_movie_grid.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this, this);

        rv_movie_grid.setAdapter(movieAdapter);
    }

    public void getFavorites(){
        getSupportActionBar().setTitle(getString(R.string.favtitle));
        querySelection = MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + " = 1";
        showLoading();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
    }

    @Override
    public void onItemClick(Cursor itemCursor) {
        //on thumbnail or item is clicked start movie detail intent with
        //the thumbnail url and movie id as extras for retrieval of selected movie details
        Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetails.class);
        movieDetailIntent.putExtra("movieId", String.valueOf(itemCursor.getInt(INDEX_MOVIE_ID)));
        movieDetailIntent.putExtra("imgpath", itemCursor.getString(INDEX_MOVIE_IMAGE));
        movieDetailIntent.putExtra("title", itemCursor.getString(INDEX_MOVIE_TITLE));
        movieDetailIntent.putExtra("description", itemCursor.getString(INDEX_MOVIE_DESCRIPTION));
        movieDetailIntent.putExtra("release", itemCursor.getString(INDEX_MOVIE_RELEASE));
        movieDetailIntent.putExtra("duration", itemCursor.getString(INDEX_MOVIE_DURATION));
        movieDetailIntent.putExtra("rating", itemCursor.getString(INDEX_MOVIE_RATING));
        movieDetailIntent.putExtra("favorite", itemCursor.getString(INDEX_MOVIE_FAVORITE));
        movieDetailIntent.putExtra("sorttype", itemCursor.getString(INDEX_MOVIE_SORT_TYPE));
        movieDetailIntent.putExtra("sort", itemCursor.getInt(INDEX_MOVIE_SORT));
        startActivity(movieDetailIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int recpos = gridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("recpos", recpos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            int recpos = savedInstanceState.getInt("recpos");
            rv_movie_grid.scrollToPosition(recpos);
        }
    }

    //TODO fix loading menu click
}

