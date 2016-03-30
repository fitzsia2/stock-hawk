package com.sam_chordas.android.stockhawk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.sam_chordas.android.stockhawk.APIs.Yahoo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Fetches a stock's history
 */
public class FetchHistoryTask extends AsyncTask<String, Void, JSONObject> {
    private static final String LOG_TAG = FetchHistoryTask.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected JSONObject doInBackground(String... params) {
        if (params.length < 3) {
            assert false;
            return null;
        }
        String symbol = params[0];
        String startDate = params[1];
        String endDate = params[2];
        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append(Yahoo.YQL_BASE + "?q=");
            urlStringBuilder.append(
                    URLEncoder.encode(
                            "select * from "
                                    + Yahoo.YAHOO_FINANCE_HISTORY_TABLE
                                    + " where symbol = \"" + symbol + "\""
                                    + " and startDate = \"" + startDate + "\""
                                    + " and endDate = \"" + endDate
                                    + "\"&diagnostics=true&env=store://datatables.org/alltableswithkeys",
                            "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

/*
https://query.yahooapis.com/v1/public/yql?q=select+*+from+yahoo.finance.historicaldata+where+symbol+%3D+YHOO+and+startDate+%3D+%222009-08-11%22++and+endDate+%3D+%222010-08-11%22%26diagnostics%3Dtrue%26env%3Dstore%3A%2F%2Fdatatables.org%2Falltableswithkeys
yahoo.finance.historicaldata
yahoo.finance.historicaldata
 */
        Log.v(LOG_TAG, ": " + urlStringBuilder.toString());

        JSONObject jsonObject = null;
        String fetchDataResponse = null;
        try {
            fetchDataResponse = fetchData(urlStringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, ": " + fetchDataResponse);

        return jsonObject;
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
