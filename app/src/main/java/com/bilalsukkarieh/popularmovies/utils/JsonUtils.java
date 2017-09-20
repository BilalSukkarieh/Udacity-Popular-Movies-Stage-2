package com.bilalsukkarieh.popularmovies.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bilalsukkarieh.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class JsonUtils {
    private final static String TAG = "getjson";
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final static String IMAGE_SIZE_PARAM = "w185";
    private static HttpUtils httpUtils = new HttpUtils();
    private static URL imageURL;
    private static String movieTitle;
    private static String movieRating;
    private static String movieOverview;
    private static String movieDuration;
    private static String movieProductionDate;
    private final static String BASE_TRAILER_URL = "https://www.youtube.com/watch?v=";
    private static String trailerURL;
    private static final String BASE_TRAILER_THUMBNAIL_URL = "http://img.youtube.com/vi/";
    private static final String TRAILER_THUMBNAIL_PATH = "/0.jpg";

    public static ArrayList<HashMap<String,String>> extractMovieData(Context context, String jsonString){
        ArrayList<HashMap<String,String>> movieData = new ArrayList<>();
        HashMap<String,String> hashMap;
        if (jsonString != null) {
            //try retrieving json data and catch any error
            try {
                //set the retreived data to a json object to handle it
                JSONObject jsonObj = new JSONObject(jsonString);
                //results are stored in json array
                JSONArray movies = jsonObj.getJSONArray("results");
                //loop the json array to get data of each movie
                movieData.clear();
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject movie = movies.getJSONObject(i);
                    //get the required data id is retrieved to be used for movie details
                    String posterPath = movie.getString("poster_path");
                    String movieId = String.valueOf(movie.getInt("id"));

                    //build uri of the thumbnail
                    Uri imageUri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                            .appendPath(IMAGE_SIZE_PARAM)
                            .appendEncodedPath(posterPath)
                            .build();
                    //convert the uri to url
                    try{
                        imageURL = new URL(imageUri.toString());

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    String url = "http://api.themoviedb.org/3/movie/"+movieId+"?api_key=" + context.getString(R.string.moviedbapi);
                    String jsonStr = httpUtils.makeServiceCall(url);

                    if (jsonStr != null) {
                        //try getting required data and catch any error
                        try {
                            //set the json retrieved to jsonobject and extracrt required data
                            JSONObject moviedetailJson = new JSONObject(jsonStr);
                            movieTitle = moviedetailJson.getString("original_title");
                            movieRating = moviedetailJson.getString("vote_average");
                            movieOverview = moviedetailJson.getString("overview");
                            movieDuration = moviedetailJson.getString("runtime");
                            movieProductionDate = moviedetailJson.getString("release_date");

                        } catch (final JSONException e) {
                            //catch any json error and log info
                            Log.d(TAG, e.toString());
                        }

                    } else {
                        //catch retreival related error and log info
                        Log.d(TAG, "error retrieving json");
                    }

                    //hash map is used to pass the thumbnail url and movie id
                    hashMap = new HashMap<>();

                    hashMap.put("id", movieId);
                    hashMap.put("imageURL", imageURL.toString());
                    hashMap.put("movietitle", movieTitle);
                    hashMap.put("moviedesc", movieOverview);
                    hashMap.put("moviedur", movieDuration);
                    hashMap.put("movierelease", movieProductionDate);
                    hashMap.put("movierating", movieRating);

                    movieData.add(hashMap);
                }


            } catch (final JSONException e) {
                //catch the json parsing error and get the info in log
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        } else {
            //catch the error of no data is retrieved from api
            Log.e(TAG, "Couldn't get json from server.");

        }

        return movieData;
    }

    public static ArrayList<HashMap<String,String>> extractTrailerData(Context context, String jsonString){
        ArrayList<HashMap<String,String>> trailerData = new ArrayList<>();
        HashMap<String,String> hashMap;
        if(jsonString != null){
            try{
                JSONObject jsonObj = new JSONObject(jsonString);
                JSONArray trailers = jsonObj.getJSONArray("results");
                trailerData.clear();
                for (int i = 0; i < trailers.length(); i++) {
                    JSONObject trailer = trailers.getJSONObject(i);
                    //get the required data id is retrieved to be used for movie details
                    String trailerKey = trailer.getString("key");
                    trailerURL = BASE_TRAILER_URL + trailerKey;
                    hashMap = new HashMap<>();
                    hashMap.put("trailerURL", trailerURL);
                    hashMap.put("trailerKEY", trailerKey);
                    trailerData.add(hashMap);
                }


            }catch (final JSONException e){
                //catch the json parsing error and get the info in log
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }else{
            //catch the error of no data is retrieved from api
            Log.e(TAG, "Couldn't get json from server.");
        }

        return trailerData;
    }

    public static ArrayList<HashMap<String,String>> extractReviewData(Context context, String jsonString){
        ArrayList<HashMap<String,String>> reviewData = new ArrayList<>();
        HashMap<String,String> hashMap;
        if(jsonString != null){
            try{
                JSONObject jsonObj = new JSONObject(jsonString);
                JSONArray reviews = jsonObj.getJSONArray("results");
                reviewData.clear();
                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject review = reviews.getJSONObject(i);
                    //get the required data id is retrieved to be used for movie details
                    String author = review.getString("author");
                    String content = review.getString("content");
                    hashMap = new HashMap<>();
                    hashMap.put("author", author);
                    hashMap.put("content", content);
                    reviewData.add(hashMap);
                }


            }catch (final JSONException e){
                //catch the json parsing error and get the info in log
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }else{
            //catch the error of no data is retrieved from api
            Log.e(TAG, "Couldn't get json from server.");
        }

        return reviewData;
    }

    public static String saveImgLocally(Context context, Bitmap imgBitmap, String filenameparams){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("movieImgs", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, filenameparams + ".jpg");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to
            // the OutputStream
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }

    public static String getYoutubeThumbnailURL(Context context, String youtubeMovieKey){
        String trailerimgurl = BASE_TRAILER_THUMBNAIL_URL + youtubeMovieKey + TRAILER_THUMBNAIL_PATH;
        return trailerimgurl;
    }
}
