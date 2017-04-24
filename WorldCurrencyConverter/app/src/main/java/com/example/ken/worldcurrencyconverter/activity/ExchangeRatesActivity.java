package com.example.ken.worldcurrencyconverter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.ken.worldcurrencyconverter.R;
import com.example.ken.worldcurrencyconverter.adapter.CurrencyAdapter;
import com.example.ken.worldcurrencyconverter.model.Rates;
import com.example.ken.worldcurrencyconverter.webclient.ApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ExchangeRatesActivity extends AppCompatActivity {
    private static final String TAG = ExchangeRatesActivity.class.getSimpleName();
    private static final int DEBOUNCE_THRESHOLD = 1000; // in ms
    private static final int GRID_LAYOUT_SPAN_COUNT = 2; // show 2 cards width wise

    // Layout Views
    private EditText mDollarsEditText;
    private EditText mCentsEditText;
    private RecyclerView mRecyclerViewCurrencies;

    // Recycler View Dependencies
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;

    // Web Client
    private ApiInterface _apiService;

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

        // Setup Web Client
        _apiService = ApiClient.getClient().create(ApiInterface.class);

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
                    }
                })
                // On the IO thread
                .observeOn(Schedulers.io())
                .map(new Function<String, Rates>() {
                    @Override
                    public Rates apply(String s) throws Exception {
                        // TODO Do network call to fetch rates

                        // TODO Return back the rates
                        return null;
                    }
                })
                // On the UI Thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Rates>() {
                    @Override
                    public void accept(Rates rates) throws Exception {
                        // TODO Hide progress bar

                        // TODO Update the UI with the new rates
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
