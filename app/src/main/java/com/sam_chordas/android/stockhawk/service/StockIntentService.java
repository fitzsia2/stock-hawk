package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 * <p/>
 * Handles running our stock task service.
 */
public class StockIntentService extends IntentService {
    public static final String ARG_TAG = "tag";
    public static final String ARG_TAG_ADD = "add";
    public static final String BUNDLE_SYMBOL = "symbol";
    private Context mContext;
    private Handler mHandler;

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra(ARG_TAG).equals(ARG_TAG_ADD)) {
            args.putString(BUNDLE_SYMBOL, intent.getStringExtra(BUNDLE_SYMBOL));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        int runResult = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(ARG_TAG), args));

        // Display a toast if there was a failure
        if (runResult == GcmNetworkManager.RESULT_FAILURE) {
            String result = getString(R.string.error_symbol_not_found, intent.getStringExtra(BUNDLE_SYMBOL));
            mHandler.post(new DisplayToast(result));
        }
    }

    private class DisplayToast implements Runnable {
        String mText;

        public DisplayToast(String text) {
            mText = text;
        }

        public void run() {
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }
}
