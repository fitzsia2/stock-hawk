package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.activities.DetailsActivity;
import com.sam_chordas.android.stockhawk.ui.activities.MyStocksActivity;

/**
 * Created by AndrewF on 4/5/2016.
 * <p>
 * Handles basic widget functions
 */
public class StocksWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DATA_UPDATED = "com.sam_chordas.android.stockhawk.app.ACTION_DATA_UPDATED";
    private static final String LOG_TAG = StocksWidgetProvider.class.getSimpleName();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stock_list);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.simple_price_widget, pendingIntent);

            // Setup the data adapter
            views.setRemoteAdapter(R.id.widget_stock_list,
                    new Intent(context, SimplePriceRemoteViewsService.class));

            // Setup a pending intent that is used by the children of the listView
            Intent clickIntent = new Intent(context, DetailsActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_stock_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_stock_list, R.id.simple_price_empty_view);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        Log.v(LOG_TAG, "Received action: " + action);

        if (action.equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_stock_list);
        }
    }
}
