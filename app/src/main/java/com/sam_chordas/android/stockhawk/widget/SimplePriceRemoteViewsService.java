package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.activities.DetailsActivity;

/**
 * Controls how widget views are populated
 */
public class SimplePriceRemoteViewsService extends RemoteViewsService {
    static private final String LOG_TAG = SimplePriceRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private final String[] QUOTE_COLUMNS = {
                    "DISTINCT " + QuoteColumns.SYMBOL,
                    QuoteColumns.BIDPRICE,
                    QuoteColumns.PERCENT_CHANGE,
            };
            private static final int COL_SYMBOL = 0;
            private static final int COL_PRICE = 1;
            private static final int COL_PER_CHANGE = 2;
            private Cursor mCursor;

            @Override
            public void onCreate() {
                Log.v(LOG_TAG, "onCreate()");
            }

            @Override
            public void onDataSetChanged() {
                Log.v(LOG_TAG, "Data set has changed!");
                if (mCursor != null)
                    mCursor.close();
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();


                mCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{"DISTINCT " + QuoteColumns.SYMBOL},
                        null, null, // selection
                        null);
                assert mCursor != null;

                int numSymbols = mCursor.getCount();

                // Get the latest entries
                mCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        QUOTE_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"}, // selection
                        QuoteColumns.CREATED + " DESC LIMIT " + numSymbols);
                assert mCursor != null;


                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        mCursor == null || !mCursor.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.simple_price);

                mCursor.moveToPosition(position);
                String stockSymbol = mCursor.getString(COL_SYMBOL);
                String stockPrice = "$" + mCursor.getString(COL_PRICE);
                String stockChange = mCursor.getString(COL_PER_CHANGE);

                views.setTextViewText(R.id.widget_stock_symbol, stockSymbol);
                views.setTextViewText(R.id.widget_stock_price, stockPrice);
                views.setTextViewText(R.id.widget_stock_change, stockChange);

                // Setup our on-click
                final Intent fillInIntent = new Intent(getApplicationContext(), DetailsActivity.class)
                        .putExtra(DetailsActivity.STOCK_SYMBOL, stockSymbol);
                views.setOnClickFillInIntent(R.id.widget_stack_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.simple_price);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
