package com.example.ken.worldcurrencyconverter.model;

/**
 * Created by ken on 2017-04-24.
 */

public class Rates {
    String currencyCode;
    Double rate;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
