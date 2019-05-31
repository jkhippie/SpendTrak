package com.danasoft.spendtrak.ui.control;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.danasoft.spendtrak.R;
import com.danasoft.spendtrak.TextUtils;
import com.danasoft.spendtrak.listener.OnControlInteractionListener;
import com.danasoft.spendtrak.listener.TextWatchListener;
import com.danasoft.spendtrak.model.Transaction;
import com.danasoft.spendtrak.ui.SpendTrakViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SearchControl extends Fragment
        implements TextWatchListener,
        RadioGroup.OnCheckedChangeListener,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private OnControlInteractionListener mListener;
    private EditText et_amount_search_input1, et_amount_search_input2;
    private Button btn_search_go, btn_search_cancel;
    private RadioGroup rg_visualize_amount_search, rg_visualize_date_search, rg_visualize_search_by;
    private TextView tv_date_search_input1, tv_date_search_input2;
    private LinearLayout ll_amount_search_inputs, ll_date_search_inputs;
    private int mSearchType;
    private SpendTrakViewModel mViewModel;
    private boolean input1HasData, input2HasData;
    private Calendar bDate;
    private Calendar aDate;
    private Scene dateSearch, amountSearch;

    public SearchControl() {}
    public static SearchControl newInstance() {
        SearchControl fragment = new SearchControl();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_control, container, false);
        et_amount_search_input1 = v.findViewById(R.id.et_amount_search_input1);
        et_amount_search_input2 = v.findViewById(R.id.et_amount_search_input2);
        btn_search_go = v.findViewById(R.id.btn_search_go);
        btn_search_cancel = v.findViewById(R.id.btn_search_cancel);
        rg_visualize_amount_search = v.findViewById(R.id.rg_visualize_amount_search);
        rg_visualize_date_search = v.findViewById(R.id.rg_visualize_date_search);
        rg_visualize_search_by = v.findViewById(R.id.rg_visualize_search_by);
        tv_date_search_input1 = v.findViewById(R.id.tv_date_search_input1);
        tv_date_search_input2 = v.findViewById(R.id.tv_date_search_input2);
        ll_amount_search_inputs = v.findViewById(R.id.ll_amount_search_inputs);
        ll_date_search_inputs = v.findViewById(R.id.ll_date_search_inputs);

        ViewGroup vg = v.findViewById(R.id.search_frame);
        dateSearch = Scene.getSceneForLayout(vg, R.layout.scene_date_search, getContext());
        amountSearch = Scene.getSceneForLayout(vg, R.layout.scene_amount_search, getContext());
        return v;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        tv_date_search_input1.setOnClickListener(this);
//        tv_date_search_input2.setOnClickListener(this);
//        et_amount_search_input1.addTextChangedListener(
//                new CurrencyInputTextWatcher(et_amount_search_input1, this));
//        et_amount_search_input2.addTextChangedListener(
//                new CurrencyInputTextWatcher(et_amount_search_input2, this));
//        rg_visualize_amount_search.setOnCheckedChangeListener(this);
//        rg_visualize_date_search.setOnCheckedChangeListener(this);
        rg_visualize_search_by.check(R.id.rb_visualize_byamount);
        rg_visualize_search_by.setOnCheckedChangeListener(this);
//        btn_search_cancel.setOnClickListener(this);
//        btn_search_go.setOnClickListener(this);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(SpendTrakViewModel.class);
    }
    @Override public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_search_cancel)
            mListener.onControlInteraction(ControlFactory.SCANCEL, null);
        if (id == R.id.btn_search_go) performSearch();
        if (id == R.id.tv_date_search_input1) showDatePicker(tv_date_search_input1);
        if (id == R.id.tv_date_search_input2) showDatePicker(tv_date_search_input2);
    }
    @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        TextView tv = (TextView) view.getTag();
        tv.setText(
                String.format(Locale.getDefault(),
                        "%d-%d-%d",
                        dayOfMonth, monthOfYear + 1, year));
        int tvId = tv.getId();
        if (tvId == R.id.tv_date_search_input1) {
            hasData(tvId, true);
            bDate = Calendar.getInstance();
            bDate.set(year, monthOfYear, dayOfMonth);
        }
        if (tvId == R.id.tv_date_search_input2) {
            hasData(tvId, true);
            aDate = Calendar.getInstance();
            aDate.set(year, monthOfYear, dayOfMonth);
        }
    }
    @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_visualize_byamount:
                showAmountSearch();
                break;
            case R.id.rb_visualize_bydate:
                showDateSearch();
                break;
            case R.id.rb_after:
                mSearchType = ControlFactory.AFTER;
                syncSearchView();
                break;
            case R.id.rb_before:
                mSearchType = ControlFactory.BEFORE;
                syncSearchView();
                break;
            case R.id.rb_on_the_day:
                mSearchType = ControlFactory.ONTHEDAY;
                syncSearchView();
                break;
            case R.id.rb_between_dates:
                mSearchType = ControlFactory.D_BETWEEN;
                syncSearchView();
                break;
            case R.id.rb_between_amounts:
                mSearchType = ControlFactory.BETWEEN;
                syncSearchView();
                break;
            case R.id.rb_greater:
                mSearchType = ControlFactory.GREATER;
                syncSearchView();
                break;
            case R.id.rb_less:
                mSearchType = ControlFactory.LESS;
                syncSearchView();
                break;
            case R.id.rb_exact_amount:
                mSearchType = ControlFactory.X_AMOUNT;
                syncSearchView();
                break;
        }
    }
    public void setListener(OnControlInteractionListener listener) { mListener = listener; }
    private void showDatePicker(final TextView tv) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                    this, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setTag(tv);
        datePickerDialog.show();
    }
    private void cancelSearch() {
        et_amount_search_input1.setText("");
        et_amount_search_input2.setText("");
        et_amount_search_input2.setVisibility(View.GONE);
        rg_visualize_amount_search.check(R.id.rb_greater);
    }
    private void displayEmptyAmountsToast() {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Please enter amount%s", (mSearchType == ControlFactory.BETWEEN) ? "s" :""),
                Toast.LENGTH_LONG).show();
    }
    private void displayEmptyDatesToast() {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Please enter date%s", (mSearchType == ControlFactory.BETWEEN) ? "s" :""),
                Toast.LENGTH_LONG).show();
    }
    private void showDateSearch() {
//        ll_amount_search_inputs.setVisibility(View.GONE);
//        ll_date_search_inputs.setVisibility(View.VISIBLE);
//        rg_visualize_amount_search.setVisibility(View.GONE);
//        rg_visualize_date_search.setVisibility(View.VISIBLE);
//        rg_visualize_date_search.check(R.id.rb_before);
        TransitionManager.go(dateSearch);

    }
    private void showAmountSearch() {
//        ll_amount_search_inputs.setVisibility(View.VISIBLE);
//        ll_date_search_inputs.setVisibility(View.GONE);
//        rg_visualize_amount_search.setVisibility(View.VISIBLE);
//        rg_visualize_date_search.setVisibility(View.GONE);
//        rg_visualize_amount_search.check(R.id.rb_greater);
        TransitionManager.go(amountSearch);
    }
    private void showAmountInput1() {
        if (et_amount_search_input1.getVisibility() == View.INVISIBLE)
            et_amount_search_input1.setVisibility(View.VISIBLE);
        if (et_amount_search_input2.getVisibility() == View.VISIBLE)
            et_amount_search_input2.setVisibility(View.INVISIBLE);
    }
    private void showAmountInput2() {
        if (et_amount_search_input1.getVisibility() == View.VISIBLE)
            et_amount_search_input1.setVisibility(View.INVISIBLE);
        if (et_amount_search_input2.getVisibility() == View.INVISIBLE)
            et_amount_search_input2.setVisibility(View.VISIBLE);
    }
    private void showAmountInput1and2() {
        if (et_amount_search_input1.getVisibility() == View.INVISIBLE)
            et_amount_search_input1.setVisibility(View.VISIBLE);
        if (et_amount_search_input2.getVisibility() == View.INVISIBLE)
            et_amount_search_input2.setVisibility(View.VISIBLE);
    }
    private void showDateInput1() {
        if (tv_date_search_input1.getVisibility() == View.INVISIBLE)
            tv_date_search_input1.setVisibility(View.VISIBLE);
        if (tv_date_search_input2.getVisibility() == View.VISIBLE)
            tv_date_search_input2.setVisibility(View.INVISIBLE);
    }
    private void showDateInput2() {
        if (tv_date_search_input1.getVisibility() == View.VISIBLE)
            tv_date_search_input1.setVisibility(View.INVISIBLE);
        if (tv_date_search_input2.getVisibility() == View.INVISIBLE)
            tv_date_search_input2.setVisibility(View.VISIBLE);
    }
    private void showDateInput1and2() {
        if (tv_date_search_input1.getVisibility() == View.INVISIBLE)
            tv_date_search_input1.setVisibility(View.VISIBLE);
        if (tv_date_search_input2.getVisibility() == View.INVISIBLE)
            tv_date_search_input2.setVisibility(View.VISIBLE);
    }
    private void syncSearchView() {
        switch (mSearchType) {
            case ControlFactory.GREATER:
                showAmountInput1();
                break;
            case ControlFactory.LESS:
                showAmountInput2();
                break;
            case ControlFactory.BETWEEN:
                showAmountInput1and2();
                break;
            case ControlFactory.X_AMOUNT:
                showAmountInput1();
                break;
            case ControlFactory.BEFORE:
                showDateInput1();
                break;
            case ControlFactory.AFTER:
                showDateInput2();
                break;
            case ControlFactory.ONTHEDAY:
                showDateInput1();
                break;
            case ControlFactory.D_BETWEEN:
                showDateInput1and2();
                break;
        }
    }
    private void performSearch() {
        switch (mSearchType) {
            case ControlFactory.AFTER:
            case ControlFactory.BEFORE:
            case ControlFactory.D_BETWEEN:
            case ControlFactory.ONTHEDAY:
                performDateSearch();
                    break;
            case ControlFactory.GREATER:
            case ControlFactory.LESS:
            case ControlFactory.BETWEEN:
            case ControlFactory.X_AMOUNT:
                performAmountSearch();
                break;
        }
    }
    private void performDateSearch() {
//        Calendar bDate = TextUtils.cleanDateInput(tv_date_search_input1.getText().toString());
//        Calendar aDate = TextUtils.cleanDateInput(tv_date_search_input2.getText().toString());

        switch (mSearchType) {
            case ControlFactory.BEFORE:
                if (!input1HasData) {
                    displayEmptyDatesToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForBefore(bDate));
                }
                break;
            case ControlFactory.AFTER:
                if (!input2HasData) {
                    displayEmptyDatesToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForAfter(aDate));
                }
                break;
            case ControlFactory.ONTHEDAY:
                if (!input1HasData) {
                    displayEmptyDatesToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForOnTheDay(bDate));
                }
                break;
            case ControlFactory.D_BETWEEN:
                if (!input1HasData) {
                    displayEmptyDatesToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForDBetween(bDate, aDate));
                }
                break;
        }
    }
    private void performAmountSearch() {
        double lAmount = TextUtils.cleanCurrencyInput(et_amount_search_input1.getText().toString());
        double hAmount = TextUtils.cleanCurrencyInput(et_amount_search_input2.getText().toString());
        boolean et1HasData = lAmount > 0;
        boolean et2HasData = hAmount > 0;

        switch (mSearchType) {
            case ControlFactory.GREATER:
                if (!et1HasData) {
                    displayEmptyAmountsToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForGreater(lAmount));
                }
                break;
            case ControlFactory.LESS:
                if (!et2HasData) {
                    displayEmptyAmountsToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForLess(hAmount));
                }
                break;
            case ControlFactory.BETWEEN:
                if (!et1HasData && !et2HasData) {
                    displayEmptyAmountsToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForBetween(lAmount, hAmount));
                }
                break;
            case ControlFactory.X_AMOUNT:
                if (!et1HasData && !et2HasData) {
                    displayEmptyAmountsToast();
                    return;
                } else {
                    mListener.onControlInteraction(
                            ControlFactory.TRESULTS,
                            searchForAmount(lAmount));
                }
                break;
        }
    }
    private List<Transaction> searchForBetween(double lAmount, double hAmount) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : mViewModel.getTransactionList()) {
            double amount = t.getTransactionAmount();
            if ((amount > lAmount) && (amount < hAmount)) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForLess(double hAmount) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : mViewModel.getTransactionList()) {
            double amt = t.getTransactionAmount();
            if (amt < hAmount) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForGreater(double lAmount) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : mViewModel.getTransactionList()) {
            double amt = t.getTransactionAmount();
            if (amt > lAmount) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForAmount(double amount) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : mViewModel.getTransactionList()) {
            double amt = t.getTransactionAmount();
            if (amt == amount) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForBefore(Calendar lDate) {
        List<Transaction> results = new ArrayList<>();
        Calendar tDay = Calendar.getInstance();
        for (Transaction t : mViewModel.getTransactionList()) {
             tDay.setTimeInMillis(t.getTransactionTimeStamp());
             boolean isBefore = tDay.before(lDate);
            if (!isBefore) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForAfter(Calendar eDate) {
        List<Transaction> results = new ArrayList<>();
        Calendar tDay = Calendar.getInstance();
        for (Transaction t : mViewModel.getTransactionList()) {
            tDay.setTimeInMillis(t.getTransactionTimeStamp());
            if (!tDay.after(eDate)) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForDBetween(Calendar lDate, Calendar eDate) {
        List<Transaction> results = new ArrayList<>();
        Calendar tDay = Calendar.getInstance();
        for (Transaction t : mViewModel.getTransactionList()) {
            tDay.setTimeInMillis(t.getTransactionTimeStamp());
            if (tDay.before(lDate) && tDay.after(eDate)) {
                results.add(t);
            }
        }
        return results;
    }
    private List<Transaction> searchForOnTheDay(Calendar date) {
        List<Transaction> results = new ArrayList<>();
        Calendar tDay = Calendar.getInstance();
        for (Transaction t : mViewModel.getTransactionList()) {
            tDay.setTimeInMillis(t.getTransactionTimeStamp());
            if (tDay.equals(date)) {
                results.add(t);
            }
        }
        return results;
    }
    @Override public void hasData(int resId, boolean has) {
        switch (resId) {
            case R.id.et_amount_search_input1:
            case R.id.tv_date_search_input1:
                input1HasData = has;
                break;
            case R.id.et_amount_search_input2:
            case R.id.tv_date_search_input2:
                input2HasData = has;
                break;
        }
    }
}
