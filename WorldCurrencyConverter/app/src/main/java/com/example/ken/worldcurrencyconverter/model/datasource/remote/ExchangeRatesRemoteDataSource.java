package com.example.ken.worldcurrencyconverter.model.datasource.remote;

import com.example.ken.worldcurrencyconverter.model.datasource.ExchangeRatesDataSource;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.model.datasource.local.ExchangeRatesLocalDataSource;
import com.example.ken.worldcurrencyconverter.webclient.FixerIOApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

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
    }

    @Override
    public Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode) {
        mLastRemotelyRefreshed.put(baseCurrencyCode, Calendar.getInstance());

        return mApiService.getLatestExchangeRates(baseCurrencyCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Calendar lastRemotelyRefreshed(String baseCurrencyCode) {
        Calendar c = mLastRemotelyRefreshed.get(baseCurrencyCode);

        return c;
    }
}
