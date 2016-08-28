package com.benavides.ramon.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;

import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.database.MoviesContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
        } finally {
            reader.close();
        }

        return sb.toString();
    }

  /*  public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
*/
    public static String readStringPreference(Context context, String preferenceToRead) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        String preference = preferences.getString(preferenceToRead, context.getString(R.string.popular_low));
        return preference;
    }

    public static void writeStringPreference(Context context,String preferenceToWrite, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferenceToWrite, value);
        editor.apply();
    }

}
