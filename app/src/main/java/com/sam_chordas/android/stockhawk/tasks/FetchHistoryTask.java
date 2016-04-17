package com.sam_chordas.android.stockhawk.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.APIs.Yahoo;
import com.sam_chordas.android.stockhawk.R;
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
    private static final String ENCODING = "UTF-8";
    private Context mContext;
    private LineChart mChart;

    private OkHttpClient client = new OkHttpClient();

    public FetchHistoryTask(Context c, LineChart chart) {
        mContext = c;
        mChart = chart;
    }


    /*--------------------------------------------
        Interface method
     -------------------------------------------*/
    private Callback mCallbackCaller;

    public void setCallbackCaller(Callback callbackCaller) {
        mCallbackCaller = callbackCaller;
    }

    public interface Callback {
        void loadedDetails(@Yahoo.YahooStatus int status);
    }
    /*------------------------------------------*/


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
                            ENCODING));
            urlStringBuilder.append("&diagnostics=true&env="); // web service does not parse ampersands well...
            urlStringBuilder.append(
                    URLEncoder.encode("store://datatables.org/alltableswithkeys",
                            ENCODING));
            urlStringBuilder.append("&format=json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONArray results = null;
        try {
            Log.v(LOG_TAG, urlStringBuilder.toString());
            String fetchDataResponse = fetchData(urlStringBuilder.toString());
            JSONObject jsonObject = (JSONObject) new JSONObject(fetchDataResponse).get(Yahoo.YAHOO_JSON_FIELD_QUERY);

            if (jsonObject.getInt(Yahoo.YAHOO_JSON_FIELD_COUNT) == 0)
                return null;

            jsonObject = jsonObject.getJSONObject(Yahoo.YAHOO_JSON_FIELD_RESULTS);

            results = jsonObject.getJSONArray(Yahoo.YAHOO_JSON_FIELD_QUOTE);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    protected void onPostExecute(JSONArray results) {
        if (results == null) {
            Log.e(LOG_TAG, "History results were null!");
            mCallbackCaller.loadedDetails(Yahoo.YAHOO_STATUS_INVALID);
            return;
        }

        // Add the data to our array list.
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Entry> closingValues = new ArrayList<>();
        for (int i = results.length() - 1; i >= 0; i--) {
            try {
                JSONObject jObj = results.getJSONObject(i);

                // Add the date
                dates.add(jObj.getString(Yahoo.YAHOO_JSON_FIELD_DETAIL_DATE));

                int index = results.length() - 1 - i;

                // Add the closing cost
                Entry e = new Entry(((float) jObj.getDouble(Yahoo.YAHOO_JSON_FIELD_DETAIL_CLOSE)), index);
                closingValues.add(e);

            } catch (JSONException e) {
                mCallbackCaller.loadedDetails(Yahoo.YAHOO_STATUS_SERVER_INVALID);
                e.printStackTrace();
            }
        }

        // Create data sets
        LineDataSet closingDataSet = new LineDataSet(closingValues, mContext.getString(R.string.history_y_axis_label));
        closingDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);


        // Add our data sets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(closingDataSet);

        LineData data = new LineData(dates, dataSets);

        data.setDrawValues(false);

        mChart.setData(data);
        mChart.invalidate();

        mCallbackCaller.loadedDetails(Yahoo.YAHOO_STATUS_OK);

    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
