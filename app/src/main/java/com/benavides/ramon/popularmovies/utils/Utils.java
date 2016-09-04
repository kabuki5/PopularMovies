package com.benavides.ramon.popularmovies.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

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

    private static final long DAY_MILLIS = 1000 * 60 * 60 * 24;

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

    public static void writeStringPreference(Context context, String preferenceToWrite, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferenceToWrite, value);
        editor.apply();
    }

    public static long readLongPreference(Context context, String preferenceToRead) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        long preference = preferences.getLong(preferenceToRead, 0);
        return preference;
    }

    public static void writeLongPreference(Context context, String preferenceToWrite, long value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(preferenceToWrite, value);
        editor.apply();
    }

    //Return just the year
    public static String formatReleaseDate(String date) throws ArrayIndexOutOfBoundsException {
        String[] splitted = date.split("-");
        return splitted[0];
    }

    //Return just the year
    public static String formatBirthDate(String date) throws ArrayIndexOutOfBoundsException {
        if (date == null)
            return null;
        String[] splitted = date.split("-");
        StringBuilder sb = new StringBuilder();
        if (splitted.length != 3)
            return null;
        sb.append(splitted[1]);
        sb.append("-");
        sb.append(splitted[2]);
        sb.append("-");
        sb.append(splitted[0]);
        return sb.toString();
    }

    public static boolean needNotificateUpdate(Context context) {
        long lastNotif = readLongPreference(context, context.getString(R.string.last_notification_pref));
        long now = System.currentTimeMillis();
        return now - lastNotif >= (DAY_MILLIS / 2);// 12 hours
    }

    public static int getCategoryByName(Context context, String name) {
        //Obtaining category ID
        Cursor cursor = context.getContentResolver().query(MoviesContract.CategoryEntry.buildCategoryData(),
                MoviesContract.CategoryEntry.CATEGORIES_PROJECTION, MoviesContract.CategoryEntry.COLUMN_NAME + "=? ", new String[]{name}, null);

        return getCategory(cursor);

    }

    private static int getCategory(Cursor cursor) {
        int result = 0;
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public static void revealAnimateView(final View view, boolean visible) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (visible)
                view.setVisibility(View.VISIBLE);
            else
                view.setVisibility(View.GONE);
            return;
        }

// get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

// get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)

        Animator anim;
        if (visible && (view.getVisibility() == View.GONE || view.getVisibility()== View.INVISIBLE)) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            view.setVisibility(View.VISIBLE);
            anim.start();
        } else if (!visible && view.getVisibility() == View.VISIBLE) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
            anim.start();
        }

// make the view visible and start the animation
    }
}
