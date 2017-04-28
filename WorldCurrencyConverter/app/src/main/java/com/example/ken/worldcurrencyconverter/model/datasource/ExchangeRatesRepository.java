package com.example.ken.worldcurrencyconverter.model.datasource;

import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.model.datasource.local.ExchangeRatesLocalDataSource;
import com.example.ken.worldcurrencyconverter.model.datasource.remote.ExchangeRatesRemoteDataSource;

import java.util.Calendar;

import io.reactivex.Observable;

/**
 * Created by ken on 2017-04-25.
 */

public class ExchangeRatesRepository  {

    private static ExchangeRatesRepository INSTANCE;
    private static final int REMOTE_REFRESH_THRESHOLD = 30 * 60 * 1000; // 30 minutes (in ms)

    private static ExchangeRatesRemoteDataSource mExchangeRatesRemoteDataSource;
    private static ExchangeRatesLocalDataSource mExchangeRatesLocalDataSource;

    // Prevent direct instantiation
    private ExchangeRatesRepository() {
        mExchangeRatesRemoteDataSource = ExchangeRatesRemoteDataSource.getInstance();
        mExchangeRatesLocalDataSource = ExchangeRatesLocalDataSource.getInstance();
    }

    // Singleton
    public static ExchangeRatesRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExchangeRatesRepository();
        }

        return INSTANCE;
    }

    public Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode) {
        Calendar now = Calendar.getInstance();

        // If it's less than 30 minutes AND we have a local copy of the rates relative to the baseCurrencyCode, use it
        if (mExchangeRatesRemoteDataSource.lastRemotelyRefreshed(baseCurrencyCode) != null
                && (now.getTimeInMillis() - mExchangeRatesRemoteDataSource.lastRemotelyRefreshed(baseCurrencyCode).getTimeInMillis()) < REMOTE_REFRESH_THRESHOLD
                && mExchangeRatesLocalDataSource.hasLocalRates(baseCurrencyCode))
                {
            return mExchangeRatesLocalDataSource.getRates(baseCurrencyCode);
        } else {
            // Fetch remote
            return mExchangeRatesRemoteDataSource.getRates(baseCurrencyCode);
        }
    }
}
