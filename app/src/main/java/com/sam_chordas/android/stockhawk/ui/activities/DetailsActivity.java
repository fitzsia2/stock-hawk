package com.sam_chordas.android.stockhawk.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.fragments.DetailFragment;

public class DetailsActivity extends AppCompatActivity {
    static final String STOCK_SYMBOL = "stock_symbol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            String stockSymbol = getIntent().getStringExtra(STOCK_SYMBOL);

            DetailFragment fragment = DetailFragment.newInstance(stockSymbol);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.stock_details_container, fragment)
                    .commit();
        }
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setElevation(0f);
    }
}
