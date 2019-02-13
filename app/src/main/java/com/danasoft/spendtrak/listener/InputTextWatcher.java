package com.danasoft.spendtrak.listener;

import android.text.Editable;
import android.widget.EditText;

public class InputTextWatcher extends BaseTextWatcher {

    public InputTextWatcher(EditText editText, TextWatchListener listener) {
        super(editText, listener);
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        checkForData();
    }
}