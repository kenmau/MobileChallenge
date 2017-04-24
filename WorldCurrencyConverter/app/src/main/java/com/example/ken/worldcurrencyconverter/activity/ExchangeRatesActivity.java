package com.example.ken.worldcurrencyconverter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.example.ken.worldcurrencyconverter.R;
import com.example.ken.worldcurrencyconverter.adapter.CurrencyAdapter;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.webclient.ApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExchangeRatesActivity extends AppCompatActivity {
    private static final String TAG = ExchangeRatesActivity.class.getSimpleName();
    private static final int DEBOUNCE_THRESHOLD = 1000; // in ms
    private static final int GRID_LAYOUT_SPAN_COUNT = 2; // show 2 cards width wise

    // Layout Views
    private EditText mDollarsEditText;
    private EditText mCentsEditText;
    private RecyclerView mRecyclerViewCurrencies;

    // Recycler View Dependencies
    private CurrencyAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;

    // Web Client
    private ApiInterface mApiService;

    // RxAndroid Cleanup
    private Disposable _disposable;

    // Setup RxAndroid Observables
    private Observable<String> createDollarsTextChangeObservable() {
        final Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // When the user inputs/deletes text, emit a signal to let consumers know
                        e.onNext(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                // Register the callback
                mDollarsEditText.addTextChangedListener(watcher);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        // Unregister the callback
                        mDollarsEditText.removeTextChangedListener(watcher);
                    }
                });
            }
        });

        // TODO: Make sure input string is a number
        // Add a de-bounce "filter" so we're not constantly emitting
        return textChangeObservable
                .debounce(DEBOUNCE_THRESHOLD, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);


        // Initialize layout properties
        mDollarsEditText = (EditText) findViewById(R.id.etDollars);
        mCentsEditText = (EditText) findViewById(R.id.etCents);
        mRecyclerViewCurrencies = (RecyclerView) findViewById(R.id.rvCurrencies);

        // Setup Recycler View Dependencies
        mRecyclerViewLayoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT);
        mRecyclerViewCurrencies.setLayoutManager(mRecyclerViewLayoutManager);

        mRecyclerViewCurrencies.setItemAnimator(new SlideInUpAnimator());

        mRecyclerViewAdapter = new CurrencyAdapter();
        mRecyclerViewCurrencies.setAdapter(mRecyclerViewAdapter);


        // Setup Web Client
        mApiService = ApiClient.getClient().create(ApiInterface.class);

//        call.enqueue(new Callback<ExchangeRatesResponse>() {
//            @Override
//            public void onResponse(Call<ExchangeRatesResponse> call, Response<ExchangeRatesResponse> response) {
//                Log.d(TAG, "Exchange Rates Successfully Received");
//                ExchangeRatesResponse exchangeRates = response.body();
//            }
//
//            @Override
//            public void onFailure(Call<ExchangeRatesResponse> call, Throwable t) {
//                Log.e(TAG, "Exchange Rates Receive Failed");
//                Log.e(TAG, t.toString());
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Observable<String> dollarsTextChangeStream = createDollarsTextChangeObservable();

        _disposable = dollarsTextChangeStream
                // On the UI Thread
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        // TODO Show a progress bar while doing work
                        Log.d(TAG, "TODO: Show Progress Bar");
                    }
                })
                // On the IO thread
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        Log.d(TAG, "Map Values");

                        // Do network call to fetch rates
                        // TODO use the appropirate currency conversion
                        Observable<ExchangeRatesResponse> call = mApiService.getLatestExchangeRates("CAD");

                        call.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ExchangeRatesResponse>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.d(TAG, "On Subscribe");
                                    }

                                    @Override
                                    public void onNext(ExchangeRatesResponse value) {
                                        Log.d(TAG, "Exchange Rates Successfully Received");
                                        mRecyclerViewAdapter.setRates(value.getRates());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // TODO Handle errors
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                        return true;
                    }
                })
                // On the UI Thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        Log.d(TAG, "TODO: Hide Progress Bar");
                        // TODO Hide progress bar
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (_disposable != null && !_disposable.isDisposed()) {
            _disposable.dispose();
        }
    }
}
