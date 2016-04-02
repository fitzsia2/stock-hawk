package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.tasks.FetchHistoryTask;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {
    static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    static final String STOCK_SYMBOL = "stock_symbol";

    private String mStockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get the current date
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        GregorianCalendar gregToday = new GregorianCalendar(Locale.US);
        String endDate = sdf.format(gregToday.getTime());
        gregToday.roll(GregorianCalendar.YEAR, -1);
        String startDate = sdf.format(gregToday.getTime());

        // Get our stock symbol
        mStockSymbol = getIntent().getStringExtra(STOCK_SYMBOL);
        TextView tv = (TextView) findViewById(R.id.activity_detail_stock_symbol);
        tv.setText(mStockSymbol);

        // Find our chart and query for a years worth of data
        LineChart lc = (LineChart) findViewById(R.id.line_chart_view);
        assert lc != null;
        FetchHistoryTask fht = new FetchHistoryTask((LineChart) findViewById(R.id.line_chart_view));
        fht.execute(mStockSymbol, startDate, endDate);

        // Show that we're displaying a years worth of data
//        RadioButton b = (RadioButton) findViewById(R.id.one_year_history_button);
//        assert b != null;
//        b.setChecked(true);
    }

//    @Override
//    public void onClick(View v) {
//
//        // Get the current date
//        String dateFormat = "yyyy-MM-dd";
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
//        GregorianCalendar gregToday = new GregorianCalendar(Locale.US);
//        String endDate = sdf.format(gregToday.getTime());
//
//
//        RadioButton rb = (RadioButton) v;
//        switch (rb.getId()) {
//            case R.id.five_year_history_button:
//                Log.v(LOG_TAG, "5Yr");
//                gregToday.roll(GregorianCalendar.YEAR, -5);
//                break;
//            case R.id.one_year_history_button:
//                Log.v(LOG_TAG, "1Yr");
//                gregToday.roll(GregorianCalendar.YEAR, -1);
//                break;
//            case R.id.six_month_history_button:
//                Log.v(LOG_TAG, "6Mo");
//                gregToday.roll(GregorianCalendar.MONTH, -6);
//                break;
//            case R.id.one_week_history_button:
//                Log.v(LOG_TAG, "1We");
//                gregToday.roll(GregorianCalendar.DAY_OF_WEEK, -7);
//                break;
//            default:
//                break;
//        }
//
//
//        // Set the start date based on selection
//        String startDate = sdf.format(gregToday.getTime());
//        FetchHistoryTask fht = new FetchHistoryTask((LineChart) findViewById(R.id.line_chart_view));
//        fht.execute(mStockSymbol, startDate, endDate);
//
//        Log.v(LOG_TAG, "Start: " + startDate + "\nEnd: " + endDate);
//    }
}
