package com.example.ken.worldcurrencyconverter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ken.worldcurrencyconverter.R;
import com.example.ken.worldcurrencyconverter.model.Rates;

/**
 * Created by ken on 2017-04-23.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private Rates mRatesData;

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
        // TODO
        holder.currencyCode.setText("USD");
        holder.currencyValue.setText("123.45");
    }

    @Override
    public int getItemCount() {
        // TODO
        return 20;
    }

    public void setRates(Rates rates) {
        mRatesData = rates;
        notifyDataSetChanged();
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
