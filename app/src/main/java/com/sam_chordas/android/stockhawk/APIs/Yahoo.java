package com.sam_chordas.android.stockhawk.APIs;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used for interacting with Yahoo's web APIs
 */
public class Yahoo {
    public static final String YQL_BASE = "https://query.yahooapis.com/v1/public/yql";
    public static final String YAHOO_FINANCE_QUOTES_TABLE = "yahoo.finance.quotes";
    public static final String YAHOO_FINANCE_HISTORY_TABLE = "yahoo.finance.historicaldata";
    public static final int YAHOO_STATUS_OK = 0;
    public static final int YAHOO_STATUS_SERVER_DOWN = 1;
    public static final int YAHOO_STATUS_SERVER_INVALID = 2;
    public static final int YAHOO_STATUS_UNKNOWN = 3;
    public static final int YAHOO_STATUS_INVALID = 4;
    @Retention(RetentionPolicy.CLASS)
    @IntDef({YAHOO_STATUS_OK, YAHOO_STATUS_SERVER_DOWN, YAHOO_STATUS_SERVER_INVALID, YAHOO_STATUS_UNKNOWN, YAHOO_STATUS_INVALID})
    public @interface YahooStatus {
    }
}
