package com.danasoft.spendtrak.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

class BaseTextWatcher implements TextWatcher {
    final WeakReference<EditText> editTextWeakReference;
    private final TextWatchListener mListener;
    private int lastLength;

    BaseTextWatcher(EditText target, TextWatchListener listener) {
        editTextWeakReference = new WeakReference<>(target);
        mListener = listener;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    void checkForData() {
        EditText et = editTextWeakReference.get();
        final int length = et.length();
        if (length != lastLength) {
            lastLength = length;
            mListener.hasData(et.getId(), et.getText().toString().length() > 0);
        }
    }
}
