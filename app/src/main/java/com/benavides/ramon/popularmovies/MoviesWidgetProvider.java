package com.benavides.ramon.popularmovies;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.benavides.ramon.popularmovies.services.WidgetIntentService;
import com.benavides.ramon.popularmovies.sync.PopularmoviesSyncAdapter;


public class MoviesWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            Intent intentToService = new Intent(context, WidgetIntentService.class);
            context.startService(intentToService);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Intent intentToService = new Intent(context, WidgetIntentService.class);
        context.startService(intentToService);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String actionIncoming = intent.getAction();
        if(actionIncoming.equals(PopularmoviesSyncAdapter.ACTION_UPDATED)){
            context.startService(new Intent(context, WidgetIntentService.class));
        }
    }
}
