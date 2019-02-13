package com.danasoft.spendtrak.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danasoft.spendtrak.ItemActionDialog;
import com.danasoft.spendtrak.JustNoteDialog;
import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.adapter.TransactionAdapter;
import com.danasoft.spendtrak.listener.CurrencyInputTextWatcher;
import com.danasoft.spendtrak.listener.InputTextWatcher;
import com.danasoft.spendtrak.listener.ItemClickSupport;
import com.danasoft.spendtrak.listener.TextWatchListener;
import com.danasoft.spendtrak.model.Merchant;
import com.danasoft.spendtrak.model.Transaction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TrackFragment
        extends Fragment
        implements View.OnClickListener, TextWatchListener, ItemActionDialog.MyDialogCallback {

    public static final String TAG = "Tracking";
    private static final long NO = -1L;
    private Context mContext;
    private SpendTrakViewModel mViewModel;
    private TextView textViewDate;
    private RecyclerView rv_track;
    private final TransactionAdapter mAdapter;
    private EditText et_merchant, et_amount;
    private boolean hasMerchant, hasAmount;
    private long needs_note = NO;

    public TrackFragment() {
        mAdapter = new TransactionAdapter();
    }

    @NotNull
    @Contract(" -> new")
    public static TrackFragment newInstance() {
        return new TrackFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.track_fragment, container, false);
        textViewDate = v.findViewById(R.id.textView);
        et_merchant = v.findViewById(R.id.et_track_merchant);
        et_amount = v.findViewById(R.id.et_track_amount);
        rv_track = v.findViewById(R.id.rv_track);
        v.findViewById(R.id.btn_add).setOnClickListener(this);
        v.findViewById(R.id.btn_add_with_note).setOnClickListener(this);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(
                getActivity())).get(SpendTrakViewModel.class);

        textViewDate.setText(TextUtils.getDate());
        et_amount.addTextChangedListener(
                new CurrencyInputTextWatcher(et_amount, this));
        et_merchant.addTextChangedListener(
                new InputTextWatcher(et_merchant, this));
        setupRecycler();
        mViewModel.getAllTransactions().observe(this, this::setAdapterData);
    }

    private void setAdapterData(List<Transaction> tList) {
        List<Merchant> merchants = mViewModel.getMerchantList();
        mAdapter.setData(tList, merchants);
        rv_track.setAdapter(null);
        rv_track.setAdapter(mAdapter);
        mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount());
        if (needs_note != NO) {
            new JustNoteDialog(Objects.requireNonNull(getActivity()),
                    mAdapter.getItemByTimestamp(getContext(), needs_note), this).show();
            needs_note = NO;
        }
    }

    private void setupRecycler() {
        rv_track.setAdapter(mAdapter);
        rv_track.setHasFixedSize(true);
        DividerItemDecoration d = new DividerItemDecoration(rv_track.getContext(), DividerItemDecoration.VERTICAL);

        rv_track.addItemDecoration(d);
        rv_track.setLayoutManager(new LinearLayoutManager(mContext));
        ItemClickSupport.addTo(rv_track)
                .setOnItemClickListener((recyclerView, position, view) -> toggleView(view))
                .setOnItemLongClickListener((recyclerView, position, view) -> {
                    showItemActionDialog(position);
                    return true;
                });
    }

    private void showItemActionDialog(final int position) {
        final Transaction t = mAdapter.getItem(mContext, position);
        t.setTmpPos(position);
        new ItemActionDialog(Objects.requireNonNull(getActivity()), t, this).show();
    }

    private void toggleView(@NotNull final View v) {
        final LinearLayout notes = v.findViewById(R.id.ll_notes);
        if (notes.getVisibility() == View.VISIBLE) {
            notes.setVisibility(View.GONE);
        } else {
            notes.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (hasMerchant && hasAmount) {
            Transaction t = getTransactionFromUi();
            long ts = t.getTransactionTimeStamp();
            saveTransaction(t);
            if (viewId == R.id.btn_add_with_note) {
                needs_note = ts;
            }
            mViewModel.hideKeyboardFrom(v);
        } else {
            complainAboutEmptyFields();
        }
    }

    private void saveTransaction(Transaction transaction) {
        if (mViewModel.insertTransaction(transaction) != -1) {
            Toast.makeText(mContext, "Transaction saved.", Toast.LENGTH_LONG).show();
            clearInput();
        } else {
            Toast.makeText(mContext, "Error saving transaction!", Toast.LENGTH_LONG).show();
        }
    }

    @NotNull
    @Contract(" -> new")
    private Transaction getTransactionFromUi() {
        return new Transaction(
                System.currentTimeMillis(),
                getMerchantId(et_merchant.getText().toString()),
                TextUtils.cleanCurrencyInput(TextUtils.cleanString(et_amount.getText().toString())),
                "No notes");
    }

    private long getMerchantId(String merchant) {
        for (Merchant m : mViewModel.getMerchantList()) {
            if (m.getMerchantName().equals(merchant))
                return m.getMerchantId();
        }
        Merchant m = new Merchant(System.currentTimeMillis(), merchant);
        mViewModel.saveMerchant(m);
        return m.getMerchantId();
    }

    private void complainAboutEmptyFields() {
        Toast.makeText(mContext, String.format(Locale.getDefault(),
                "Please enter %s%s%s",
                hasMerchant ? "" : "merchant",
                (!hasAmount && !hasMerchant) ? " and " : " ",
                hasAmount ? "" : "amount"), Toast.LENGTH_LONG).show();
    }

    private void clearInput() {
        et_merchant.setText("");
        et_amount.setText(getString(R.string.string00_00));
    }

    @Override
    public void onDestroy() {
        ItemClickSupport.removeFrom(rv_track);
        super.onDestroy();
    }

    @Override
    public void hasData(final int resId, final boolean hasData) {
        if (resId == R.id.et_track_amount)
            hasAmount = hasData;
        if (resId == R.id.et_track_merchant)
            hasMerchant = hasData;
    }

    @Override
    public void shouldNotify(int pos) {
        mAdapter.notifyItemChanged(pos);
    }
}