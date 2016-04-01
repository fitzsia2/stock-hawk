package com.sam_chordas.android.stockhawk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
import java.util.ArrayList;

/**
 * Fetches a stock's history
 */
public class FetchHistoryTask extends AsyncTask<String, Void, JSONArray> {
    private static final String LOG_TAG = FetchHistoryTask.class.getSimpleName();
    private LineChart mChart;

    private OkHttpClient client = new OkHttpClient();

    public FetchHistoryTask(LineChart chart) {
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

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject jObj = results.getJSONObject(i);
                dates.add(jObj.getString("Date"));
                Entry e = new Entry(((float) jObj.getDouble("Close")), i);
                values.add(e);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }

        LineDataSet setClosingPrices = new LineDataSet(values, "Closing Prices");
        setClosingPrices.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setClosingPrices);
        LineData data = new LineData(dates, dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
