package com.example.ken.worldcurrencyconverter.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ken on 2017-04-23.
 */

public class ExchangeRatesResponse {
    @SerializedName("base")
    String base;

    @SerializedName("date")
    String date;

    @SerializedName("rates")
    Map<String, Double> rates;

    public ExchangeRatesResponse() {
        rates = new HashMap<>();
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
