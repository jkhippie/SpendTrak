package com.danasoft.spendtrak.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TrackFragment
        extends Fragment
        implements View.OnClickListener, TextWatchListener, ItemActionDialog.MyDialogCallback, View.OnFocusChangeListener, Animation.AnimationListener {

    public static final String TAG = "Tracking";
    private static final long NO = -1L;
    private Context mContext;
    private SpendTrakViewModel mViewModel;
    private TextView tv_main_date;
    private RecyclerView rv_track;
    private final TransactionAdapter mAdapter;
    private EditText et_amount;
    private ImageView iv_add_anim;
    private AutoCompleteTextView actv_track_merchant;
    private boolean hasMerchant, hasAmount;
    private long needs_note = NO;
    private List<Merchant> mMerchants;

    public TrackFragment() { mAdapter = new TransactionAdapter(); }

    @NotNull @Contract(" -> new")
    public static TrackFragment newInstance() {
        return new TrackFragment();
    }
    @Override public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setEnterTransition(new Explode().setDuration(500));
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.track_fragment, container, false);
        tv_main_date = v.findViewById(R.id.tv_main_date);
        iv_add_anim = v.findViewById(R.id.iv_add_anim);
        actv_track_merchant = v.findViewById(R.id.actv_track_merchant);
        et_amount = v.findViewById(R.id.et_track_amount);
        rv_track = v.findViewById(R.id.rv_track);
        v.findViewById(R.id.btn_add).setOnClickListener(this);
        v.findViewById(R.id.btn_add_with_note).setOnClickListener(this);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(
                getActivity())).get(SpendTrakViewModel.class);
        mMerchants = mViewModel.getMerchantList();
//        tv_main_date.setText(TextUtils.getDate());
        et_amount.addTextChangedListener(
                new CurrencyInputTextWatcher(et_amount, this));
        et_amount.setOnFocusChangeListener(this);
        actv_track_merchant.addTextChangedListener(
                new InputTextWatcher(actv_track_merchant, this));
        actv_track_merchant.setOnFocusChangeListener(this);
        setupRecycler();
        setupActv();
        mViewModel.getAllTransactions().observe(this, this::setAdapterData);
    }
    private void setAdapterData(List<Transaction> tList) {
        List<Merchant> merchants = mViewModel.getMerchantList();
        if (!merchants.equals(mMerchants)) {
            mMerchants = merchants;
            setupActv();
        }
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
    private void setupActv() {
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1,
                getMerchantNames());
        actv_track_merchant.setThreshold(1);
        actv_track_merchant.setAdapter(actvAdapter);
    }
    private String[] getMerchantNames() {
        List<Merchant> mList = mViewModel.getMerchantList();
        String[] results = new String[mList.size()];
        int index = 0;
        for (Merchant m : mList) {
            results[index++] = m.getMerchantName();
        }
        return results;
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
    @Override public void onClick(View v) {
        mViewModel.hideKeyboardFrom(v);
        int viewId = v.getId();
        if (hasMerchant && hasAmount) {
            Transaction t = getTransactionFromUi();
            long ts = t.getTransactionTimeStamp();
            animateTransactionAdd();
            saveTransaction(t);
            if (viewId == R.id.btn_add_with_note) {
                needs_note = ts;
            }
            actv_track_merchant.requestFocus();
        } else {
            complainAboutEmptyFields();
        }
    }

    private void animateTransactionAdd() {
        AnimatorSet forwardSet = new AnimatorSet();
        AnimatorSet reverseSet = new AnimatorSet();
        forwardSet.setDuration(400);
        forwardSet.setInterpolator(new AccelerateDecelerateInterpolator());
        reverseSet.setDuration(400);
        reverseSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> forwardList=new ArrayList<Animator>();
        ArrayList<Animator> reverseList=new ArrayList<Animator>();
        ObjectAnimator scaleXForward = ObjectAnimator.ofFloat(iv_add_anim, "ScaleX", 0f, 1.2f, 1f);
        forwardList.add(scaleXForward);
        ObjectAnimator scaleYForward = ObjectAnimator.ofFloat(iv_add_anim, "ScaleY", 0f, 1.2f, 1f);
        forwardList.add(scaleYForward);
        ObjectAnimator scaleXReverse = ObjectAnimator.ofFloat(iv_add_anim, "ScaleX", 1f, 1.2f, 0f);
        reverseList.add(scaleXReverse);
        ObjectAnimator scaleYReverse = ObjectAnimator.ofFloat(iv_add_anim, "ScaleY", 1f, 1.2f, 0f);
        reverseList.add(scaleYReverse);
        ObjectAnimator translateYReverse = ObjectAnimator.ofFloat(iv_add_anim, "translationY", 0f, 700f);
        ObjectAnimator undoTranslateYReverse = ObjectAnimator.ofFloat(iv_add_anim, "translationY", 700f, 0f);
        reverseList.add(translateYReverse);
        forwardSet.playTogether(forwardList);
        reverseSet.playTogether(reverseList);
        forwardSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) { reverseSet.start(); }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        reverseSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) {
                undoTranslateYReverse.start();
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        iv_add_anim.setVisibility(View.VISIBLE);
        forwardSet.start();
    }

    private void saveTransaction(Transaction transaction) {
        if (mViewModel.insertTransaction(transaction) != -1) {
            Toast.makeText(mContext, "Transaction saved.", Toast.LENGTH_LONG).show();
            clearInput();
        } else {
            Toast.makeText(mContext, "Error saving transaction!", Toast.LENGTH_LONG).show();
        }
    }
    @NotNull @Contract(" -> new")
    private Transaction getTransactionFromUi() {
        return new Transaction(
                System.currentTimeMillis(),
                getMerchantId(actv_track_merchant.getText().toString()),
                TextUtils.cleanCurrencyInput(TextUtils.cleanString(et_amount.getText().toString())),
                "No notes");
    }
    private long getMerchantId(String merchant) {
        for (Merchant m : mViewModel.getMerchantList()) {
            if (m.getMerchantName().equalsIgnoreCase(merchant))
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
        actv_track_merchant.setText("");
        et_amount.setText(getString(R.string.string00_00));
    }
    @Override public void onDestroy() {
        ItemClickSupport.removeFrom(rv_track);
        super.onDestroy();
    }
    @Override public void hasData(final int resId, final boolean hasData) {
        if (resId == R.id.et_track_amount)
            hasAmount = hasData;
        if (resId == R.id.actv_track_merchant)
            hasMerchant = hasData;
    }
    @Override public void shouldNotify(int pos) {
        mAdapter.notifyItemChanged(pos);
    }
    @Override public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (i == R.id.et_track_amount || i == R.id.actv_track_merchant) {
            focusSwell(v, hasFocus);
        }
    }

    private void focusSwell(View v, boolean hasFocus) {
        EditText et = (EditText)v;
        if (hasFocus) {
            et.setSelection(et.getText().length());
            et.setTextSize(25);
        } else {
            et.setTextSize(21);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}