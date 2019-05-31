package com.danasoft.spendtrak.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.model.Merchant;

import java.util.ArrayList;
import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.ViewHolder> {
    private final List<Merchant> mMerchants;

    public MerchantAdapter() { mMerchants = new ArrayList<>(); }
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).
                inflate(R.layout.merchant_view, viewGroup, false);
        return new MerchantAdapter.ViewHolder(view);
    }
    @Override public void onBindViewHolder(@NonNull MerchantAdapter.ViewHolder viewHolder, int position) {
        Merchant merchant = mMerchants.get(position);
        viewHolder.tv_merchant_view_total_transactions.setText(String.valueOf(merchant.getCount()));
        viewHolder.tv_merchant_view_name.setText(merchant.getMerchantName());
        viewHolder.tv_merchant_view_total.setText(
                TextUtils.getFormattedCurrencyString(merchant.getTransactionTotal()));
        viewHolder.setIsRecyclable(false);
    }
    @Override public int getItemCount() {
        return mMerchants.size();
    }
    public Merchant getItem(int position) {
        return mMerchants.get(position);
    }
    public void setMerchantList(List<Merchant> mList) {
        mMerchants.clear();
        mMerchants.addAll(mList);
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_merchant_view_total_transactions, tv_merchant_view_name, tv_merchant_view_total;

        ViewHolder(View v) {
            super(v);
            tv_merchant_view_total_transactions = v.findViewById(R.id.tv_merchant_view_total_transactions);
            tv_merchant_view_name = v.findViewById(R.id.tv_merchant_view_name);
            tv_merchant_view_total = v.findViewById(R.id.tv_merchant_view_total);
        }
    }
}