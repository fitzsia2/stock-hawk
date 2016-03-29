package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

public class DetailsActivity extends AppCompatActivity {
    static final String STOCK_SYMBOL = "stock_symbol";
    private String mStockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mStockSymbol = getIntent().getStringExtra(STOCK_SYMBOL);

        TextView tv = (TextView) findViewById(R.id.activity_detail_stock_symbol);
        tv.setText(mStockSymbol);
    }

}
