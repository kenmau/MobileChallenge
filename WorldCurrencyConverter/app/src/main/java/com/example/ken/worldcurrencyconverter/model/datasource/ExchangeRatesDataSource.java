package com.example.ken.worldcurrencyconverter.model.datasource;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ken.worldcurrencyconverter.ApplicationController;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.google.gson.Gson;

import java.util.Calendar;

import io.reactivex.Observable;

/**
 * Created by ken on 2017-04-25.
 */

public interface ExchangeRatesDataSource {

    interface Remote {

        Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode);

        Calendar lastRemotelyRefreshed();

    }

    interface Local {

        Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode);

        void saveRates(ExchangeRatesResponse response);

        boolean hasLocalRates(String baseCurrencyCode);

    }
}
