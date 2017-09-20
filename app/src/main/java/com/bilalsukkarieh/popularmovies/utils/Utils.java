package com.bilalsukkarieh.popularmovies.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Utils {

    public static URL getURL(String s){
        //convert a string to url
        Uri itemUri = Uri.parse(s);
        URL itemURL = null;
        try{
            itemURL = new URL(itemUri.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        return itemURL;
    }

    public static Bitmap DownloadImage(String imageUrl)
    {
        Bitmap bitmap = null;
        InputStream input = null;
        try {
            input = new HttpUtils().OpenHttpConnection(imageUrl);
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }


}
