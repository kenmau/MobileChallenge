package com.example.ken.worldcurrencyconverter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.ken.worldcurrencyconverter.R;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.webclient.ApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeRatesActivity extends AppCompatActivity {
    private static final String TAG = ExchangeRatesActivity.class.getSimpleName();

    // Layout Views
    private EditText _etDollars;
    private EditText _etCents;

    // Web Client
    ApiInterface _apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        // Setup Web Client
        _apiService = ApiClient.getClient().create(ApiInterface.class);

        // Initialize layout properties
        _etDollars = (EditText) findViewById(R.id.etDollars);
        _etCents = (EditText) findViewById(R.id.etCents);
    }
}
