package com.example.ken.worldcurrencyconverter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ken.worldcurrencyconverter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ken on 2017-04-23.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private List<String> mCurrencyCode;
    private List<Double> mRates;
    private double baseValue;

    public CurrencyAdapter() {
    }

    @Override
    public CurrencyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.currency_list_item, parent, false);
        return new CurrencyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CurrencyAdapter.ViewHolder holder, int position) {
        holder.currencyCode.setText(mCurrencyCode.get(position));
        holder.currencyValue.setText(String.format("%.2f", mRates.get(position)*baseValue));
    }

    @Override
    public int getItemCount() {

        if (mCurrencyCode == null) {
            return 0;
        }

        return mCurrencyCode.size();
    }

    @Override
    public long getItemId(int position) {
        return mCurrencyCode.get(position).hashCode();
    }

    public void clearRates() {
        int oldSize = 0;

        if (this.mCurrencyCode != null) {
            oldSize = this.mCurrencyCode.size();
        }

        this.mCurrencyCode = new ArrayList<>();
        this.mRates = new ArrayList<>();
        this.baseValue = 0.0;

        notifyItemRangeRemoved(0, oldSize);
    }

    public void setRates(Map<String, Double> rates, double baseValue) {
        this.mCurrencyCode = new ArrayList<>(rates.keySet());
        this.mRates = new ArrayList<>(rates.values());
        this.baseValue = baseValue;

        notifyItemRangeInserted(0, mCurrencyCode.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView currencyCode;
        private TextView currencyValue;

        public ViewHolder(View itemView) {
            super(itemView);
            currencyCode = (TextView) itemView.findViewById(R.id.tvListItemCurrencyCode);
            currencyValue = (TextView) itemView.findViewById(R.id.tvListItemCurrencyValue);
        }
    }
}
