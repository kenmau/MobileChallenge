package com.example.ken.worldcurrencyconverter.model.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ken.worldcurrencyconverter.ApplicationController;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.model.datasource.ExchangeRatesDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * Created by ken on 2017-04-25.
 */

public class ExchangeRatesLocalDataSource implements ExchangeRatesDataSource.Local {

    private static ExchangeRatesLocalDataSource INSTANCE;
    private static final String PREFS_RATES = "PrefsRates";


    // Prevent direct instantiation
    private ExchangeRatesLocalDataSource() {

    }

    // Singleton
    public static ExchangeRatesLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExchangeRatesLocalDataSource();
        }

        return INSTANCE;
    }


    @Override
    public Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode) {
        ExchangeRatesResponse ratesResponse = null;
        // Try to load from persistence storage
        Gson g = new Gson();
        SharedPreferences settings = ApplicationController.getAppContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String j = settings.getString(PREFS_RATES + "." + baseCurrencyCode, "");

        if (j != null && !j.isEmpty() && !j.equals("null")) {
            Type type = new TypeToken<ExchangeRatesResponse>(){}.getType();
            ratesResponse = g.fromJson(j, type);
        }

        if (ratesResponse != null) {
            return Observable.just(ratesResponse);
        }

        // Should throw error instead
        return null;
    }

    public void saveRates(ExchangeRatesResponse response) {
        // Persist Rates
        Gson g = new Gson();

        SharedPreferences settings = ApplicationController.getAppContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = settings.edit();

        e.putString(PREFS_RATES + "." + response.getBase(), g.toJson(response));
        e.commit();
    }

    public boolean hasLocalRates(String baseCurrencyCode) {
        // Check if exists from persistence storage
        Gson g = new Gson();
        SharedPreferences settings = ApplicationController.getAppContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String j = settings.getString(PREFS_RATES + "." + baseCurrencyCode, "");

        if (j != null && !j.isEmpty() && !j.equals("null")) {
            return true;
        }

        return false;
    }
}
