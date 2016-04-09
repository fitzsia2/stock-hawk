package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.activities.MyStocksActivity;

/**
 * Updates the widget when the StocksSyncService has updated our data.
 * <p>
 * Based on Udacity's Advanced_Android_Development project
 */
public class WidgetIntentService extends IntentService {
    private static final String LOG_TAG = WidgetIntentService.class.getSimpleName();
    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
    };
    private static final int COL_SYMBOL = 0;
    private static final int COL_BIDPRICE = 1;


    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StocksWidgetProvider.class));


        // Look up how many stocks we're watching
        Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{"DISTINCT " + QuoteColumns.SYMBOL},
                null, null, // selection
                null);
        assert c != null;
        int numSymbols = c.getCount();

        String symbol = "";
        String bidPrice = "0";

        // Get the latest entries
        c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                QUOTE_COLUMNS,
                null, null, // selection
                QuoteColumns.CREATED + " DESC LIMIT " + numSymbols);
        assert c != null;
        while (c.moveToNext()) {
            symbol = c.getString(COL_SYMBOL);
            bidPrice = c.getString(COL_BIDPRICE);
            Log.v(LOG_TAG, "Symbol: " + symbol + " @ $" + bidPrice);
        }
        c.close();


        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_price;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.widget_stock_symbol, symbol);
            views.setTextViewText(R.id.widget_stock_price, "$" + bidPrice);
//            views.setTextViewText(R.id.widget_stock_price, "$" + NumberFormat.getInstance().format(bidPrice));

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
