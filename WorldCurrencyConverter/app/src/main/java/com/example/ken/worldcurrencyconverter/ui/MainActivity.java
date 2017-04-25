package com.example.ken.worldcurrencyconverter.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ken.worldcurrencyconverter.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Have MainActivity launch a new activity in case we want to swap and do A/B testing
        Intent intent = new Intent(this, ExchangeRatesActivity.class);
        startActivity(intent);

        // TODO onBack() seems to go back to this activity from ExchangeRatesActivity.  Need to handle that
    }
}
