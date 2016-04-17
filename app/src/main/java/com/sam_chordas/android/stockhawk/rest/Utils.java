package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sam_chordas.android.stockhawk.APIs.Yahoo;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 *
 * General utility methods used throughout the application.
 */
public class Utils {

    public static boolean showPercent = true;
    private static String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray resultsArray;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject(Yahoo.YAHOO_JSON_FIELD_QUERY);
                int count = Integer.parseInt(jsonObject.getString(Yahoo.YAHOO_JSON_FIELD_COUNT));
                String created = jsonObject.getString(Yahoo.YAHOO_JSON_FIELD_CREATED);
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject(Yahoo.YAHOO_JSON_FIELD_RESULTS)
                            .getJSONObject(Yahoo.YAHOO_JSON_FIELD_QUOTE);
                    batchOperations.add(buildBatchOperation(jsonObject, created));
                } else {
                    resultsArray = jsonObject.getJSONObject(Yahoo.YAHOO_JSON_FIELD_RESULTS).getJSONArray(Yahoo.YAHOO_JSON_FIELD_QUOTE);

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(buildBatchOperation(jsonObject, created));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject, String created) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString(Yahoo.YAHOO_JSON_FIELD_DETAIL_CHANGE);
            if (change.equals("null")) {
                return null;
            }
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString(Yahoo.YAHOO_JSON_FIELD_DETAIL_SYMBOL));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString(Yahoo.YAHOO_JSON_FIELD_DETAIL_BID)));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString(Yahoo.YAHOO_JSON_FIELD_DETAIL_PERCENTCHANGE), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.CREATED, created);
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    /**
     * Checks with the connectivity manager to see if we can connect to the interet
     *
     * @param context current context
     * @return true if we have an internet connection
     */
    public static boolean HasConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
    }
}
