package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by AndrewF on 4/5/2016.
 * <p>
 * Handles basic widget functions
 */
public class StocksWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DATA_UPDATED = "com.sam_chordas.android.stockhawk.app.ACTION_DATA_UPDATED";
    private static final String LOG_TAG = StocksWidgetProvider.class.getSimpleName();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        context.startService(new Intent(context, WidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        Log.v(LOG_TAG, "Received action: " + action);

        if (action.equals(ACTION_DATA_UPDATED)) {
            context.startService(new Intent(context, WidgetIntentService.class));
        }
    }
}
