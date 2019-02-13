package com.danasoft.spendtrak;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.danasoft.spendtrak.model.Transaction;
import com.danasoft.spendtrak.ui.SpendTrakViewModel;

import java.util.Objects;

public class ItemActionDialog extends Dialog implements View.OnClickListener {
    private final FragmentActivity mActivity;
    private final Transaction mTransaction;
    private MyDialogCallback callback;
    private View mItemView;
    private EditText et_note;

    public ItemActionDialog(@NonNull FragmentActivity activity,
                            @NonNull Transaction t,
                            @NonNull MyDialogCallback cb) {
        super(activity);
        mActivity = activity;
        callback = cb;
        mItemView = (View)t.getTag();
        if (mItemView == null) mItemView = new View(activity);
        mTransaction = t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_action);
        ((FrameLayout)findViewById(R.id.fl_dialog_transaction_view)).addView(mItemView);
        final Button btn_dialog_edit_notes = findViewById(R.id.btn_dialog_edit_notes);
        btn_dialog_edit_notes.setOnClickListener(v -> {
            ((FrameLayout)findViewById(R.id.fl_dialog_transaction_view)).removeView(mItemView);
                    editNotes();
                }
        );
        final Button btn_dialog_delete_transaction = findViewById(R.id.btn_dialog_delete_transaction);
        btn_dialog_delete_transaction.setOnClickListener(v -> {
            ((FrameLayout)findViewById(R.id.fl_dialog_transaction_view)).removeView(mItemView);
            confirmDelete();
        });
        findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> this.dismiss());
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_dialog_edit_notes_positive:
                mTransaction.setTransactionNotes(et_note.getText().toString());
                ViewModelProviders.of(Objects.requireNonNull(mActivity))
                        .get(SpendTrakViewModel.class)
                        .updateTransaction(mTransaction);
                callback.shouldNotify(mTransaction.getTmpPos());
                this.dismiss();
                break;
            case R.id.btn_dialog_delete_confirmed:
                ViewModelProviders.of(Objects.requireNonNull(mActivity))
                        .get(SpendTrakViewModel.class)
                        .removeTransaction(mTransaction);
                callback.shouldNotify(mTransaction.getTmpPos());
                this.dismiss();
                break;
        }
    }

    private void confirmDelete() {
        setContentView(R.layout.dialog_confirm_delete);

        ((FrameLayout)findViewById(R.id.fl_dialog_confirm_delete_view)).addView(mItemView);
        findViewById(R.id.btn_dialog_delete_cancel).setOnClickListener(v -> this.dismiss());
        findViewById(R.id.btn_dialog_delete_confirmed).setOnClickListener(this);
    }

    private void editNotes() {
        setContentView(R.layout.dialog_edit_notes);
        ((TextView)findViewById(R.id.tv_dialog_edit_notes_title)).setText(R.string.edit_note_title);
        et_note = findViewById(R.id.et_dialog_note);
        final String note = mTransaction.getTransactionNotes();
        et_note.setText((note.length() > 0) ? note : "");
        final Button btn_dialog_edit_notes_positive = findViewById(R.id.btn_dialog_edit_notes_positive);
        btn_dialog_edit_notes_positive.setOnClickListener(this);
        final Button btn_dialog_edit_notes_negative = findViewById(R.id.btn_dialog_edit_notes_negative);
        btn_dialog_edit_notes_negative.setOnClickListener(v -> this.dismiss());
    }

    public interface MyDialogCallback {
        void shouldNotify(int pos);
    }
}