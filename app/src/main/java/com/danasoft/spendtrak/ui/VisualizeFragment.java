package com.danasoft.spendtrak.ui;

import android.app.AlertDialog;
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
import com.danasoft.spendtrak.SpendTrakViewModel;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.adapter.TransactionAdapter;
import com.danasoft.spendtrak.listener.ItemClickSupport;
import com.danasoft.spendtrak.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VisualizeFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "Visualize";
    private Context mContext;
    private TextView tv_total_merchants, tv_total_amount;
    private Button btn_total, btn_merchant, btn_todo;
    private RecyclerView rv_visualize;
    private List<Transaction> mTransactions;
    private List<String> merchantList = new ArrayList<>();
    private double totalAmount;
    private final TransactionAdapter mAdapter;

    public static VisualizeFragment newInstance() {
        return new VisualizeFragment();
    }

    public VisualizeFragment() {
        mAdapter = new TransactionAdapter();
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
        init();
    }

    private void init() {
        mTransactions = ViewModelProviders.of(this)
                .get(SpendTrakViewModel.class).getTransctionList();
        for (Transaction t : mTransactions) {
            String merchant = t.getTransactionMerchant();
            if (!merchantList.contains(merchant))
                merchantList.add(merchant);
            totalAmount += t.getTransactionAmount();
        }
        setupRecycler();
        calculateTotals(mTransactions);
    }

    private void calculateTotals(List<Transaction> toDisplay) {
        totalAmount = 0.0d;
        List<String> displayMerchants = new ArrayList<>();

        for (Transaction t : toDisplay) {
            String merchant = t.getTransactionMerchant();
            if (!displayMerchants.contains(merchant))
                displayMerchants.add(merchant);
            totalAmount += t.getTransactionAmount();
        }

        String s;
        if (displayMerchants.size() > 1) {
            s = "Total spending";
        } else {
            int tNum = toDisplay.size();
            s = String.format(
                    Locale.getDefault(),
                    "%d transaction%s",
                    tNum,
                    (tNum > 1) ? "s" : "");
        }
        tv_total_merchants.setText(s);
        tv_total_amount.setText(TextUtils.getFormattedCurrencyString(totalAmount));
        mAdapter.setTransactionsList(toDisplay);
    }

    private void setupRecycler() {
        //mAdapter.setTransactionsList(mTransactions);
        rv_visualize.setAdapter(mAdapter);
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
                calculateTotals(mTransactions);
                break;
            case R.id.btn_merchant:
                showSelectMerchantDialog();
                break;
            case R.id.btn_todo:

                break;
        }
    }

    private void showSelectMerchantDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        CharSequence[] merchantNames = new CharSequence[merchantList.size()];
        int index = 0;
        for (String merchant : merchantList) {
            merchantNames[index++] = merchant;
        }
        builder.setSingleChoiceItems(merchantNames, -1, (dialog, which) ->
        {
            showMerchantTransactions(merchantList.get(which));
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showMerchantTransactions(String merchant) {
        List<Transaction> displayList = new ArrayList<>();
        for (Transaction t : mTransactions) {
            if (t.getTransactionMerchant().equals(merchant)) {
                displayList.add(t);
            }
        }
        calculateTotals(displayList);
    }
}
