package com.benavides.ramon.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.benavides.ramon.popularmovies.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ramon on 15/7/16.
 */
public class Utils {

    public static String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();

        if (is == null) {
            throw new IOException("Null Input Stream");
        }

        BufferedReader reader = null;

        reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String readSortByPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        String sortBYPref = preferences.getString(context.getString(R.string.sort_by_pref),context.getString(R.string.popular_low));
        return sortBYPref;
    }

    public static void writeSortByPreference(Context context, String value){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.sort_by_pref),value);
        editor.apply();
    }
}
