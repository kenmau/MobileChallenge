package com.example.ken.worldcurrencyconverter.ui;

import android.text.TextUtils;
import android.util.Log;

import com.example.ken.worldcurrencyconverter.model.datasource.ExchangeRatesRepository;
import com.example.ken.worldcurrencyconverter.model.datasource.local.ExchangeRatesLocalDataSource;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;

import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by ken on 2017-04-25.
 */

public class ExchangeRatesPresenter implements ExchangeRatesContract.Presenter {
    private static final String TAG = ExchangeRatesPresenter.class.getSimpleName();

    // Business Data
    private Integer mDollarsAmount;
    private Integer mCentsAmount;
    private String mBaseCurrency;

    private final ExchangeRatesContract.View mView;

    public ExchangeRatesPresenter(ExchangeRatesContract.View view) {
        mView = view;

        // Initialize
        mDollarsAmount = 0;
        mCentsAmount = 0;
        mBaseCurrency = "CAD";
    }

    @Override
    public void subscribe() {
        // Any subscription tasks go here
    }

    @Override
    public void unSubscribe() {
        // Unsubscribe to anything we subscribed to
    }

    @Override
    public void setDollars(String dollarsAmount) {
        if (dollarsAmount != null) {
            // In the case it is empty string, it means a value of 0
            if (dollarsAmount.equals("")) {
                dollarsAmount = "0";
            }

            try {
                mDollarsAmount = Integer.parseInt(dollarsAmount);
                Log.d(TAG, "Setting Dollar Amount: " + mDollarsAmount.toString());
            } catch (NumberFormatException e) {
                Observable.just(true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                                       @Override
                                       public void accept(Boolean aBoolean) throws Exception {
                                           mView.showInvalidInputDollarsEditText();
                                       }
                                   });
            }
        }
    }

    @Override
    public void setCents(String centsAmount) {
        if (centsAmount != null) {
            // In the case it is empty string, it means a value of 0
            if (centsAmount.equals("")) {
                centsAmount = "0";
            } else if (centsAmount.length() == 1) {
                // The case where someone enters in 1 digit into the cents field (i.e. "5".  This actually means "50").
                centsAmount += "0";
            }

            try {
                mCentsAmount = Integer.parseInt(centsAmount);
                Log.d(TAG, "Setting Cent Amount: " + mCentsAmount.toString());
            } catch (NumberFormatException e) {
                Observable.just(true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                                       @Override
                                       public void accept(Boolean aBoolean) throws Exception {
                                           mView.showInvalidInputCentsEditText();
                                       }
                                   });

            }
        }
    }

    @Override
    public void setBaseCurrency(String baseCurrency) {
        if (!TextUtils.isEmpty(baseCurrency)) {
            Log.d(TAG, "Setting Base Currency: " + baseCurrency);
            mBaseCurrency = baseCurrency;
        } else {
            Log.e(TAG, "Null or Empty Base Currency");
        }
    }

    @Override
    public void fetchRates() {
        Log.d(TAG, "Fetching Rates");

        Observable<ExchangeRatesResponse> call;

        // Do network call to fetch rates

        call = ExchangeRatesRepository.getInstance().getRates(mBaseCurrency);
        call.subscribe(new Observer<ExchangeRatesResponse>() {

            ExchangeRatesResponse data;

            @Override
            public void onSubscribe(Disposable d) {
                mView.showProgressBar();
            }

            @Override
            public void onNext(ExchangeRatesResponse value) {
                Log.d(TAG, "Exchange Rates Successfully Received");
                data = value;

                // Save to persistent storage here
                ExchangeRatesLocalDataSource.getInstance().saveRates(value);
            }

            @Override
            public void onError(Throwable e) {
                // Handle network errors
                if (e instanceof UnknownHostException) {
                    mView.showConnectionError();
                }
                e.printStackTrace();

                mView.hideProgressBar();
            }

            @Override
            public void onComplete() {
                double combinedAmount;

                // Assumption: Cents will only have 2 decimal places.  We could have more, we just have to do extra handling.
                combinedAmount = mDollarsAmount.intValue() + mCentsAmount.intValue()/100.0;

                mView.clearRates();
                mView.setRates(data.getRates(), combinedAmount);

                mView.hideProgressBar();
            }
        });
    }

    public Integer getmDollarsAmount() {
        return mDollarsAmount;
    }

    public Integer getmCentsAmount() {
        return mCentsAmount;
    }

    public String getmBaseCurrency() {
        return mBaseCurrency;
    }
}
