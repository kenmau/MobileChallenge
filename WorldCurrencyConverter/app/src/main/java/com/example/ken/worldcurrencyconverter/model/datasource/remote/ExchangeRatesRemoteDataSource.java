package com.example.ken.worldcurrencyconverter.model.datasource.remote;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ken.worldcurrencyconverter.ApplicationController;
import com.example.ken.worldcurrencyconverter.model.datasource.ExchangeRatesDataSource;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.model.datasource.local.ExchangeRatesLocalDataSource;
import com.example.ken.worldcurrencyconverter.webclient.FixerIOApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ken on 2017-04-25.
 */

public class ExchangeRatesRemoteDataSource implements ExchangeRatesDataSource.Remote {
    private static ExchangeRatesRemoteDataSource INSTANCE;

    private static final String PREFS_LAST_REFRESHED = "PrefsLastRefreshed";

    // Web Client
    private ApiInterface mApiService;

    private static Map<String, Calendar> mLastRemotelyRefreshed;

    // Singleton
    public static ExchangeRatesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExchangeRatesRemoteDataSource();
        }

        return INSTANCE;
    }

    // Prevent direct instantiation
    private ExchangeRatesRemoteDataSource() {
        // Setup Web Client
        mApiService = FixerIOApiClient.getClient().create(ApiInterface.class);

        // Setup Map to store last refreshed dates with respect to the currency
        mLastRemotelyRefreshed = new HashMap<>();

        // Try to load from persistence storage
        Gson g = new Gson();
        SharedPreferences settings = ApplicationController.getAppContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String j = settings.getString(PREFS_LAST_REFRESHED, "");

        if (j != null && !j.isEmpty() && !j.equals("null")) {
            Type type = new TypeToken<Map<String, Calendar>>(){}.getType();
            mLastRemotelyRefreshed = g.fromJson(j, type);
        }
    }

    @Override
    public Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode) {
        mLastRemotelyRefreshed.put(baseCurrencyCode, Calendar.getInstance());

        // Persist
        Gson g = new Gson();

        SharedPreferences settings = ApplicationController.getAppContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = settings.edit();

        e.putString(PREFS_LAST_REFRESHED, g.toJson(mLastRemotelyRefreshed));
        e.commit();

        return mApiService.getLatestExchangeRates(baseCurrencyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Calendar lastRemotelyRefreshed(String baseCurrencyCode) {
        Calendar c = mLastRemotelyRefreshed.get(baseCurrencyCode);

        return c;
    }
}
