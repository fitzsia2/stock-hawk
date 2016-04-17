package com.sam_chordas.android.stockhawk.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.sam_chordas.android.stockhawk.APIs.Yahoo;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.tasks.FetchHistoryTask;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements FetchHistoryTask.Callback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "stock_symbol";

    // TODO: Rename and change types of parameters
    private String mStockSymbol;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stockSymbol the stock symbol
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(String stockSymbol) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, stockSymbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockSymbol = getArguments().getString(ARG_PARAM1);
            assert mStockSymbol != null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.detail_fragment, container, false);

        // Get the current date
        final String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        GregorianCalendar gregToday = new GregorianCalendar(Locale.US);
        String endDate = sdf.format(gregToday.getTime());
        gregToday.roll(GregorianCalendar.YEAR, -1);
        String startDate = sdf.format(gregToday.getTime());

        // Get our stock symbol and company name
        TextView tv = (TextView) ll.findViewById(R.id.activity_detail_stock_symbol);
        assert tv != null;
        tv.setText(mStockSymbol);

        // Find our chart and query for a years worth of data
        LineChart lc = (LineChart) ll.findViewById(R.id.line_chart_view);
        assert lc != null;
        FetchHistoryTask fht = new FetchHistoryTask(getContext(), (LineChart) ll.findViewById(R.id.line_chart_view));
        fht.setCallbackCaller(this);
        fht.execute(mStockSymbol, startDate, endDate);
        lc.setDescription("");

        return ll;
    }

    @Override
    public void loadedDetails(@Yahoo.YahooStatus int results) {
        ProgressBar pg = (ProgressBar) getActivity().findViewById(R.id.line_chart_progress_bar);
        pg.setVisibility(View.GONE);

        LineChart lc = (LineChart) getActivity().findViewById(R.id.line_chart_view);
        lc.setVisibility(View.VISIBLE);

        if (results != Yahoo.YAHOO_STATUS_OK) {
            lc.setBackgroundColor(0x000000);
        }
    }
}
