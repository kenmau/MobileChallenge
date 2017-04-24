package com.example.ken.worldcurrencyconverter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ken on 2017-04-23.
 */

public class ExchangeRatesResponse {
    @SerializedName("base")
    String base;

    @SerializedName("date")
    String date;

    @SerializedName("rates")
    Rates rates;

    public ExchangeRatesResponse() {
    }

//    public static ExchangeRatesResponse parseJSON(String response) {
//        Gson gson = new GsonBuilder().create();
//        ExchangeRatesResponse exchangeRatesResponse = gson.fromJson(response, ExchangeRatesResponse.class);
//        return exchangeRatesResponse;
//    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Rates getRates() {
        return rates;
    }
}
