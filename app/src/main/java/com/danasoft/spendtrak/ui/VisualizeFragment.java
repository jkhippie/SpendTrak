package com.danasoft.spendtrak.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.adapter.MerchantAdapter;
import com.danasoft.spendtrak.adapter.TransactionAdapter;
import com.danasoft.spendtrak.listener.ItemClickSupport;
import com.danasoft.spendtrak.model.Merchant;
import com.danasoft.spendtrak.model.Transaction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VisualizeFragment
        extends Fragment
        implements View.OnClickListener {
    public static final String TAG = "Visualize";
    private Context mContext;
    private TextView tv_total_merchants, tv_total_amount;
    private Button btn_total, btn_merchant, btn_todo;
    private RecyclerView rv_visualize;
    private final List<Transaction> mTransactions;
    private final List<Merchant> mMerchants;
    private final TransactionAdapter mTransactionAdapter;
    private final MerchantAdapter mMerchantAdapter;

    @NotNull
    @Contract(" -> new")
    public static VisualizeFragment newInstance() {
        return new VisualizeFragment();
    }

    public VisualizeFragment() {
        mTransactions = new ArrayList<>();
        mMerchants = new ArrayList<>();
        mTransactionAdapter = new TransactionAdapter();
        mMerchantAdapter = new MerchantAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visualize_fragment, container, false);
        tv_total_merchants = v.findViewById(R.id.tv_total_merchants);
        tv_total_amount = v.findViewById(R.id.tv_total_amount);
        btn_total = v.findViewById(R.id.btn_total);
        btn_merchant = v.findViewById(R.id.btn_merchant);
        btn_todo = v.findViewById(R.id.btn_todo);
        rv_visualize = v.findViewById(R.id.rv_visualize);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_total.setOnClickListener(this);
        btn_merchant.setOnClickListener(this);
        btn_todo.setOnClickListener(this);
        initData();
        calculateTotals();
        setupRecycler();
        showTransactions(mTransactions);
    }

    private void initData() {
        SpendTrakViewModel mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(SpendTrakViewModel.class);
        mTransactions.addAll(mViewModel.getTransactionList());
        mMerchants.addAll(mViewModel.getMerchantList());
    }

    private void calculateTotals() {
        double totalAmount = 0.0d;

        for (Transaction t : mTransactions) {
            totalAmount += t.getTransactionAmount();
        }
        tv_total_merchants.setText(String.format(Locale.getDefault(),
                "%d merchants", mMerchants.size()));
        tv_total_amount.setText(TextUtils.getFormattedCurrencyString(totalAmount));
    }

    private void setupRecycler() {
        mTransactionAdapter.setData(mTransactions, mMerchants);
        mMerchantAdapter.setMerchantList(mMerchants);
        rv_visualize.setAdapter(mTransactionAdapter);
        rv_visualize.setHasFixedSize(true);
        rv_visualize.setLayoutManager(new LinearLayoutManager(mContext));
        ItemClickSupport.addTo(rv_visualize)
                .setOnItemClickListener((recyclerView, position, view) -> {

                })
                .setOnItemLongClickListener((recyclerView, position, view) -> false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_total:
                calculateTotals();
                showTransactions(mTransactions);
                break;
            case R.id.btn_merchant:
                showMerchants(mMerchants);
                break;
            case R.id.btn_todo:

                break;
        }
    }

    private void showMerchants(List<Merchant> mList) {
        mMerchantAdapter.setMerchantList(mList);
        rv_visualize.setAdapter(null);
        rv_visualize.setAdapter(mMerchantAdapter);
    }

    private void showTransactions(List<Transaction> tList) {
        mTransactionAdapter.setData(tList, mMerchants);
        rv_visualize.setAdapter(null);
        rv_visualize.setAdapter(mTransactionAdapter);
    }
}
