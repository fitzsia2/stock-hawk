package com.sam_chordas.android.stockhawk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.APIs.Yahoo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Fetches a stock's history
 */
public class FetchHistoryTask extends AsyncTask<String, Void, JSONArray> {
    private static final String LOG_TAG = FetchHistoryTask.class.getSimpleName();
    private LineChartView mChart;

    private OkHttpClient client = new OkHttpClient();

    public FetchHistoryTask(LineChartView chart) {
        mChart = chart;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if (params.length != 3) throw new AssertionError();
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
                                    + " and endDate = \"" + endDate + "\"",
                            "UTF-8"));
            urlStringBuilder.append("&diagnostics=true&env="); // web service does not parse ampersands well...
            urlStringBuilder.append(
                    URLEncoder.encode("store://datatables.org/alltableswithkeys",
                            "UTF-8"));
            urlStringBuilder.append("&format=json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONArray results = null;
        try {
            Log.v(LOG_TAG, urlStringBuilder.toString());
            String fetchDataResponse = fetchData(urlStringBuilder.toString());
            JSONObject jsonObject = (JSONObject) new JSONObject(fetchDataResponse).get("query");

            if (jsonObject.getInt("count") == 0)
                return null;

            jsonObject = jsonObject.getJSONObject("results");

            results = jsonObject.getJSONArray("quote");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    protected void onPostExecute(JSONArray results) {
        if (results == null) {
            Log.e(LOG_TAG, "History results were null!");
            return;
        }

        int count = results.length();

        // Create the data set
        String[] dates = new String[count];
        float[] values = new float[count];
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject jObj = results.getJSONObject(i);

                // Add the date
                dates[i] = jObj.getString("Date");

                // Add the closing cost
                values[i] = (float) jObj.getDouble("Close");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LineSet dataSet = new LineSet(dates, values);
        mChart.addData(dataSet);
        mChart.show();
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
