package com.benavides.ramon.popularmovies.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.benavides.ramon.popularmovies.MainActivity;
import com.benavides.ramon.popularmovies.MoviesWidgetProvider;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WidgetIntentService extends IntentService {

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetsIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MoviesWidgetProvider.class));

        Uri popularUri = MoviesContract.MovieEntry.buildMoviesDataWithCategory(Utils.getCategoryByName(this, getString(R.string.popular_low)));
        Cursor data = getContentResolver().query(popularUri, MoviesContract.MovieEntry.MOVIES_PROJECTION, null, null, MoviesContract.MovieEntry.COLUMN_RATING + " DESC");

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String poster = data.getString(MoviesContract.MovieEntry.MOVIES_COLUMN_POSTER);

        for (int appWidgetId : widgetsIds) {
            try {
                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_view);


                views.setImageViewBitmap(R.id.widget_poster, downloadUrl(poster));

                Intent intentToMain = new Intent(this, MainActivity.class);
                intentToMain.putExtra("poster_path", poster);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentToMain, PendingIntent.FLAG_CANCEL_CURRENT);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private Bitmap downloadUrl(String strUrl) throws IOException {

        Bitmap bitmap = null;
        InputStream iStream = null;
        try {
            URL url = new URL(strUrl);
            /** Creating an http connection to communcate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /** Connecting to url */
            urlConnection.connect();

            /** Reading data from url */
            iStream = urlConnection.getInputStream();

            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);

        } catch (Exception e) {
        } finally {
            if (iStream != null)
                iStream.close();
        }
        return bitmap;
    }

}
