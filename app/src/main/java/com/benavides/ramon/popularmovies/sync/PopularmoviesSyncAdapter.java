package com.benavides.ramon.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.benavides.ramon.popularmovies.MainActivity;
import com.benavides.ramon.popularmovies.R;
import com.benavides.ramon.popularmovies.data.Category;
import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.data.Review;
import com.benavides.ramon.popularmovies.data.Trailer;
import com.benavides.ramon.popularmovies.database.MoviesContract;
import com.benavides.ramon.popularmovies.utils.DataHelper;
import com.benavides.ramon.popularmovies.utils.Utils;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Sync Adapter Class
 */
public class PopularmoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 180; //3 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static final int NOTIFICATION_ID = 100;
    public static final String ACTION_UPDATED = "com.benavides.ramon.popularmovies.ACTION.SYNC_UPDATED";

    public PopularmoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("RBM", " Performing sync with SyncAdapter!!!");

        // The BIG Cookie!!!! // The big cookie is not possible since we have just 40 max requests every 10 seconds
        Context context = getContext();

        //getting all categories to request TMDB server
        ArrayList<Category> categories = getCategories(context);
        for (Category category : categories) {
            String movieCategory = category.getName();
            if (movieCategory != null) {
                //   request to API to obtain movies data
                ContentValues[] movieValues = DataHelper.retrieveMoviesData(getContext(), movieCategory, 1);

                //get category id
                int categoryId = Utils.getCategoryByName(getContext(), category.getName());

//              delete movies from Movies table and from Movie-Category table
                context.getContentResolver().delete(MoviesContract.MovieCategoryEntry.buildMovieCategoryData(), null, new String[]{Integer.toString(categoryId)});

                // insert data into database
                context.getContentResolver().bulkInsert(MoviesContract.MovieEntry.buildMoviesDataWithCategory(categoryId), movieValues);
            }
        }
//              delete actors data
        context.getContentResolver().delete(MoviesContract.ActorsEntry.buildActorsData(), null, null);
        syncWidgets();
        notifySync();
    }

    private void syncWidgets() {
        Intent updateIntent = new Intent(ACTION_UPDATED).setPackage(getContext().getPackageName());
        getContext().sendBroadcast(updateIntent);
    }


    private void notifySync() {
        //take control over a notification per day

        if (!Utils.needNotificateUpdate(getContext()))
            return;

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setColor(getContext().getResources().getColor(R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(getContext().getString(R.string.app_name))
                        .setContentText("Popular Movies Updated database");

        Intent resultIntent = new Intent(getContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Utils.writeLongPreference(getContext(), getContext().getString(R.string.last_notification_pref), System.currentTimeMillis());
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        PopularmoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Calling setSyncAutomatically to be enabled the periodic sync
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Start sync
         */
        //syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }


    // get all categories and make category array list from cursor
    private ArrayList<Category> getCategories(Context context) {
        Cursor cursor = null;
        ArrayList<Category> categories = new ArrayList<>();
        try {
            cursor = context.getContentResolver().query(MoviesContract.CategoryEntry.buildCategoryData(),
                    MoviesContract.CategoryEntry.CATEGORIES_PROJECTION,
                    MoviesContract.CategoryEntry.COLUMN_NAME + " <> ?",
                    new String[]{context.getString(R.string.favorites_low)},
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setId(cursor.getInt(MoviesContract.CategoryEntry.CATEGORIES_COLUMN_ID));
                    category.setName(cursor.getString(MoviesContract.CategoryEntry.CATEGORIES_COLUMN_NAME));
                    categories.add(category);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return categories;

    }

    // Main method to call Sync Adapter
    public static void initSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
