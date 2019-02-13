package com.danasoft.spendtrak;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.danasoft.spendtrak.model.Transaction;
import com.danasoft.spendtrak.ui.SpendTrakViewModel;

import java.util.Objects;

public class JustNoteDialog extends Dialog implements View.OnClickListener {
    private final FragmentActivity mActivity;
    private final Transaction mTransaction;
    private ItemActionDialog.MyDialogCallback callback;
    private EditText et_note;

    public JustNoteDialog(@NonNull FragmentActivity activity,
                            @NonNull Transaction t,
                            @NonNull ItemActionDialog.MyDialogCallback cb) {
        super(activity);
        mActivity = activity;
        callback = cb;
        mTransaction = t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_edit_notes);
        ((TextView)findViewById(R.id.tv_dialog_edit_notes_title)).setText(R.string.edit_note_title);
        et_note = findViewById(R.id.et_dialog_note);
        final Button btn_dialog_edit_notes_positive = findViewById(R.id.btn_dialog_edit_notes_positive);
        btn_dialog_edit_notes_positive.setOnClickListener(this);
        final Button btn_dialog_edit_notes_negative = findViewById(R.id.btn_dialog_edit_notes_negative);
        btn_dialog_edit_notes_negative.setOnClickListener(v -> this.dismiss());
    }

    @Override
    public void onClick(View v) {
        mTransaction.setTransactionNotes(et_note.getText().toString());
        ViewModelProviders.of(Objects.requireNonNull(mActivity))
                .get(SpendTrakViewModel.class)
                .updateTransaction(mTransaction);
        callback.shouldNotify(mTransaction.getTmpPos());
        this.dismiss();
    }
}