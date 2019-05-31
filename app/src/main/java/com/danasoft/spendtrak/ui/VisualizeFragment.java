package com.danasoft.spendtrak.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danasoft.spendtrak.ItemActionDialog;
import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.adapter.MerchantAdapter;
import com.danasoft.spendtrak.adapter.TransactionAdapter;
import com.danasoft.spendtrak.listener.ItemClickSupport;
import com.danasoft.spendtrak.listener.OnControlInteractionListener;
import com.danasoft.spendtrak.listener.TextWatchListener;
import com.danasoft.spendtrak.model.Merchant;
import com.danasoft.spendtrak.model.Transaction;
import com.danasoft.spendtrak.ui.control.ControlFactory;
import com.danasoft.spendtrak.ui.control.SearchControl;
import com.danasoft.spendtrak.ui.control.VisualizeButtonsControl;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VisualizeFragment extends Fragment
        implements View.OnClickListener,
        TextWatchListener,
        ItemActionDialog.MyDialogCallback,
        OnControlInteractionListener {

    public static final String TAG = "Visualize";
    private Context mContext;
    private SpendTrakViewModel mViewModel;
    private TextView tv_total_merchants, tv_total_amount;
    private RecyclerView rv_visualize;
    private final List<Transaction> mTransactions;
    private final List<Merchant> mMerchants;
    private final TransactionAdapter mTransactionAdapter;
    private final MerchantAdapter mMerchantAdapter;
    protected static boolean isMerchants;

    @NotNull @Contract(" -> new")
    public static VisualizeFragment newInstance() {
        return new VisualizeFragment();
    }
    public VisualizeFragment() {
        mTransactions = new ArrayList<>();
        mMerchants = new ArrayList<>();
        mTransactionAdapter = new TransactionAdapter();
        mMerchantAdapter = new MerchantAdapter();
    }
    @Override public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getContext();
    }
    @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visualize_fragment, container, false);
        setHasOptionsMenu(true);
        tv_total_merchants = v.findViewById(R.id.tv_total_merchants);
        tv_total_amount = v.findViewById(R.id.tv_total_amount);
        rv_visualize = v.findViewById(R.id.rv_visualize);
        return v;
    }
    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(SpendTrakViewModel.class);
        initData();
        displayTotals();
        setupRecycler();
        showTransactions(mViewModel.getTransactionList());
        showControl(ControlFactory.BUTTONS);
    }
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_db) {
            mViewModel.clearDb();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onClick(View v) {
        mViewModel.hideKeyboardFrom(v);
        switch (v.getId()) {
            case R.id.btn_total:
                resetView();
                break;
            case R.id.btn_merchant:
                showMerchants(mMerchants);
                break;
            case R.id.btn_search:
                //toggleButtonsAndSearch();
                showControl("search");
                break;
        }
    }
    private void initData() {
        SpendTrakViewModel mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(SpendTrakViewModel.class);
        mTransactions.addAll(mViewModel.getTransactionList());
        mMerchants.addAll(mViewModel.getMerchantList());
    }
    private void displayTotals() {
        double totalAmount = 0.0d;

        for (Transaction t : mTransactions) {
            totalAmount += t.getTransactionAmount();
        }
        tv_total_merchants.setText(String.format(Locale.getDefault(),
                "%d merchants", mMerchants.size()));
        tv_total_amount.setText(TextUtils.getFormattedCurrencyString(totalAmount));
    }
    private void displayMerchantTotals(@NotNull Merchant merchant) {
        double totalAmount = 0.0d;
        List<Transaction> tList = merchant.getTransactions();

        for (Transaction t : tList) {
            totalAmount += t.getTransactionAmount();
        }
        tv_total_merchants.setText(String.format(Locale.getDefault(),
                "%d transactions", tList.size()));
        tv_total_amount.setText(TextUtils.getFormattedCurrencyString(totalAmount));
    }
    private void setupRecycler() {
        mTransactionAdapter.setData(mViewModel.getTransactionList(), mMerchants);
        mMerchantAdapter.setMerchantList(mMerchants);
        rv_visualize.setAdapter(mTransactionAdapter);
        DividerItemDecoration d = new DividerItemDecoration(rv_visualize.getContext(), DividerItemDecoration.VERTICAL);
        rv_visualize.addItemDecoration(d);
        rv_visualize.setHasFixedSize(true);
        rv_visualize.setLayoutManager(new LinearLayoutManager(mContext));
        ItemClickSupport.addTo(rv_visualize)
                .setOnItemClickListener((recyclerView, position, view) -> {
                    if (VisualizeFragment.isMerchants)
                        onMerchantItemClicked(position);
                    else
                        onTransactionItemClicked(view);
                })
                .setOnItemLongClickListener((recyclerView, position, view) -> {
                    showItemActionDialog(position);
                    return true;
                });
    }
    private void showItemActionDialog(final int position) {
        final Transaction t = mTransactionAdapter.getItem(mContext, position);
        t.setTmpPos(position);
        new ItemActionDialog(Objects.requireNonNull(getActivity()), t, this).show();
    }
    private void onTransactionItemClicked(@NotNull final View view) {
        LinearLayout ll_notes = view.findViewById(R.id.ll_notes);
        ll_notes.setVisibility(
                ll_notes.getVisibility() == View.GONE ?
                        View.VISIBLE : View.GONE);
    }
    private void onMerchantItemClicked(final int position) {
        showTransactionsByMerchant(mMerchantAdapter.getItem(position));
    }
    private void showControl(@NotNull String which) {
        Fragment frag = new Fragment();
        switch (which) {
            case ControlFactory.BUTTONS:
                frag = ControlFactory.getVisualizeButtons();
                ((VisualizeButtonsControl)frag).setListener(VisualizeFragment.this);
                break;
            case "search":
                frag = ControlFactory.getSearchControl();
                ((SearchControl)frag).setListener(VisualizeFragment.this);
                break;
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.visualize_control_frame, frag, frag.getTag())
                .commit();
    }
    private void resetView() {
        displayTotals();
        showTransactions(mViewModel.getTransactionList());
    }
    private void displaySearchResults(List<Transaction> resultsList) {
        mTransactionAdapter.setCleanData(resultsList);
        rv_visualize.setAdapter(mTransactionAdapter);
        final int newSize = resultsList.size();
        tv_total_merchants.setText(String.format(Locale.getDefault(),
                "%s transaction%s", newSize, (newSize > 1) ? "s" : ""));
        tv_total_amount.setText(TextUtils.getFormattedCurrencyString(getTransactionsTotal(resultsList)));
    }
    private double getTransactionsTotal(@NotNull List<Transaction> tList) {
        double totalAmount = 0.0d;

        for (Transaction t : tList) { totalAmount += t.getTransactionAmount(); }
        return totalAmount;
    }
    private void showMerchants(List<Merchant> mList) {
        isMerchants = true;
        mMerchantAdapter.setMerchantList(mList);
        rv_visualize.setAdapter(null);
        rv_visualize.setAdapter(mMerchantAdapter);
        displayTotals();
    }
    private void showTransactions(List<Transaction> tList) {
        isMerchants = false;
        mTransactionAdapter.setData(tList, mMerchants);
        rv_visualize.setAdapter(null);
        rv_visualize.setAdapter(mTransactionAdapter);
        displayTotals();
    }
    private void showTransactionsByMerchant(@NotNull Merchant merchant) {
        isMerchants = false;
        mTransactionAdapter.setData(merchant.getTransactions(), mMerchants);
        rv_visualize.setAdapter(null);
        rv_visualize.setAdapter(mTransactionAdapter);
        displayMerchantTotals(merchant);
    }
    @Override public void hasData(int resId, boolean has) {}
    @Override public void shouldNotify(int pos) {
        mTransactionAdapter.notifyItemChanged(pos);
        resetView();
    }
    @Override public void onControlInteraction(String key, @Nullable List<Transaction> data) {
        switch (key) {
            case ControlFactory.VTOTAL:
                resetView();
                break;
            case ControlFactory.VMERCHANT:
                showMerchants(mMerchants);
                break;
            case ControlFactory.VSEARCH:
                showControl(ControlFactory.SEARCH);
                break;
            case ControlFactory.SCANCEL:
                showControl(ControlFactory.BUTTONS);
                break;
            case ControlFactory.TRESULTS:
                displaySearchResults(data);
                break;
        }
    }
}