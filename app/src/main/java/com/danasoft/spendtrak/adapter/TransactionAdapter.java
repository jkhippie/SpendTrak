package com.danasoft.spendtrak.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> mTransactions;

    public TransactionAdapter(){}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).
                inflate(R.layout.transaction_view, viewGroup, false);
        return new TransactionAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (mTransactions == null) return;
        Transaction transaction = mTransactions.get(position);
        String timeStamp = transaction.getTransactionTimeStamp();
        String date = TextUtils.getTrackViewDate(timeStamp);
        viewHolder.tv_date.setText(date);
        viewHolder.tv_time.setText(TextUtils
                .getTrackViewTime(transaction.getTransactionTimeStamp()));
        viewHolder.tv_merchant.setText(transaction.getTransactionMerchant());
        viewHolder.tv_amount.setText(TextUtils
                .getFormattedCurrencyString(transaction.getTransactionAmount()));
        viewHolder.tv_notes.setText(transaction.getTransactionNotes());
    }
    @Override
    public int getItemCount() {
        if (mTransactions != null)
            return mTransactions.size();
        else
            return 0;
    }

    public View getItemView(Context context, int position) {
        final ViewGroup nullParent = null;
        Transaction t = mTransactions.get(position);
        String timeStamp = t.getTransactionTimeStamp();
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_view, nullParent, false);
        ((TextView)view.findViewById(R.id.tv_date)).setText(TextUtils.getTrackViewDate(timeStamp));
        ((TextView)view.findViewById(R.id.tv_time)).setText(TextUtils.getTrackViewTime(timeStamp));
        ((TextView)view.findViewById(R.id.tv_merchant)).setText(t.getTransactionMerchant());
        ((TextView)view.findViewById(R.id.tv_amount)).setText(TextUtils.getFormattedCurrencyString(t.getTransactionAmount()));
        ((TextView)view.findViewById(R.id.tv_notes)).setText(t.getTransactionNotes());
        return view;
    }

    public Transaction getItem(Context context, int index) {
        Transaction t = mTransactions.get(index);
        t.setTag(getItemView(context, index));
        return t;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_date, tv_time, tv_merchant, tv_amount, tv_notes;

         ViewHolder(View v) {
             super(v);
             tv_date = v.findViewById(R.id.tv_date);
             tv_time = v.findViewById(R.id.tv_time);
             tv_merchant = v.findViewById(R.id.tv_merchant);
             tv_amount = v.findViewById(R.id.tv_amount);
             tv_notes = v.findViewById(R.id.tv_notes);
         }
    }

    public void setTransactionsList(final List<Transaction> transList) {
        if(transList == null || transList.isEmpty()) {
            mTransactions = new ArrayList<>(Collections.singletonList(Transaction.getNew()));
            notifyDataSetChanged();
            return;
        }
        if (mTransactions == null) {
            mTransactions = transList;
            notifyItemRangeInserted(0, mTransactions.size());
        } else {
            if (mTransactions.size() == 1 && mTransactions.get(0).getTransactionMerchant().equals(Transaction.NEW_TRANS)) {
                mTransactions.remove(0);
                mTransactions = transList;
                notifyItemRangeRemoved(0, 1);
                notifyItemRangeInserted(0, mTransactions.size());
                return;
            }
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mTransactions.size();
                }

                @Override
                public int getNewListSize() {
                    return transList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mTransactions.get(oldItemPosition).getTransactionId() ==
                            transList.get(newItemPosition).getTransactionId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Transaction newProduct = transList.get(newItemPosition);
                    Transaction oldProduct = mTransactions.get(oldItemPosition);
                    return Objects.equals(newProduct.getTransactionId(), oldProduct.getTransactionId())
                            && Objects.equals(newProduct.getTransactionMerchant(), oldProduct.getTransactionMerchant())
                            && Objects.equals(newProduct.getTransactionAmount(), oldProduct.getTransactionAmount());
                }
            });
            mTransactions = transList;
            result.dispatchUpdatesTo(this);
        }
    }
}
