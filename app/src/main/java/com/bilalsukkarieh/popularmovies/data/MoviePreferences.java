package com.bilalsukkarieh.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;


public class MoviePreferences {

    public static final String SORT_PREFERENCE_KEY = "sorting";
    public static final String POPULAR_SORT_PREFERENCE = "popular";
    public static final String TOP_SORT_PREFERENCE = "top_rated";
    public static final String FAVORITE_PREFERENCE = "favorite";


    public static String getPreferedSort(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(SORT_PREFERENCE_KEY, POPULAR_SORT_PREFERENCE);
    }

    public static void setPreferedSort(Context context, String preferedSort){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(SORT_PREFERENCE_KEY, preferedSort);
        spEditor.apply();
    }



}
