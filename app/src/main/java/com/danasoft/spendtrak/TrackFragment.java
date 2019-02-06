package com.danasoft.spendtrak;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danasoft.spendtrak.adapter.TransactionAdapter;
import com.danasoft.spendtrak.model.Transaction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TrackFragment extends Fragment implements View.OnClickListener, TextWatchListener {
    public static final String TAG = "Tracking";
    private Context mContext;
    private SpendTrakViewModel mViewModel;
    private TextView textViewDate;
    private RecyclerView rv_track;
    private final TransactionAdapter mAdapter = new TransactionAdapter();
    EditText et_merchant, et_amount;

    @NotNull
    @Contract(" -> new")
    public static TrackFragment newInstance() {
        return new TrackFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SpendTrakViewModel.class);

        textViewDate.setText(new SimpleDateFormat("dd-MMM-yyyy")
                .format(Calendar.getInstance().getTime()));
        et_amount.addTextChangedListener(new CurrencyInputTextWatcher(et_amount, this));
        setupRecycler();
        mViewModel.getAllTransactions().observe(this, mAdapter::setTransactionsList);
    }

    private void setupRecycler() {
        rv_track.setAdapter(mAdapter);
        rv_track.setHasFixedSize(true);
        rv_track.setLayoutManager(new LinearLayoutManager(mContext));
        ItemClickSupport.addTo(rv_track)
                .setOnItemClickListener((recyclerView, position, view) -> toggleView(view))
                .setOnItemLongClickListener((recyclerView, position, view) -> {
                    showItemActionDialog(position);
                    return true;
                });
    }

    private void showItemActionDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        CharSequence[] actions = new CharSequence[2];
        actions[0] = "Add note";
        actions[1] = "Delete";
        builder.setItems(actions, (dialog, which) -> {
            switch (which) {
                case 0:
                    editTransactionNotes(position);
                    dialog.dismiss();
                    break;
                case 1:
                    deleteTransaction(position);
                    dialog.dismiss();
                    break;
            }
        });
        builder.create().show();
    }

    private void deleteTransaction(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete this transaction?");
        builder.setView(mAdapter.getItemView(mContext, position));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("OK", (dialog, which) -> mViewModel.removeTransaction(mAdapter.getItem(position)));
        builder.create().show();
    }

    private void editTransactionNotes(int position) {

    }

    private void toggleView(@NotNull View v) {
        LinearLayout details = v.findViewById(R.id.ll_details);
        LinearLayout notes = v.findViewById(R.id.ll_notes);
        if (details.getVisibility() == View.VISIBLE) {
            notes.setVisibility(View.VISIBLE);
            details.setVisibility(View.GONE);
        } else {
            notes.setVisibility(View.GONE);
            details.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        String merchant = et_merchant.getText().toString();
        if (merchant.isEmpty()) return;
        String amount = et_amount.getText().toString();
        String cAmount = amount.replaceAll("[$,.]", "");
        if (cAmount.isEmpty()) return;
        if (mViewModel.addTransaction(new Transaction(
                TextUtils.getTimeStamp(),
                merchant,
                TextUtils.cleanCurrencyInput(amount),
                "No notes")) != -1) {
            Toast.makeText(mContext, "Transaction saved.", Toast.LENGTH_LONG).show();
        }
        clearInput();
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
    public void hasData(int resId, boolean has) {

    }
}