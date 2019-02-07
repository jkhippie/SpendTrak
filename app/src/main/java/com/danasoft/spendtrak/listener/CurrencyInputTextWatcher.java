package com.danasoft.spendtrak.listener;

import android.support.annotation.IdRes;
import android.text.Editable;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class CurrencyInputTextWatcher extends BaseTextWatcher {
    private final int ID;

    public CurrencyInputTextWatcher(EditText editText, @IdRes int _id, TextWatchListener listener) {
        super(editText, listener);
        ID = _id;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count){}

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;
        String s = editable.toString();
        if (s.isEmpty()) return;
        editText.removeTextChangedListener(this);
        String cleanString = s.replaceAll("[$,.]", "");
        checkForData(cleanString, ID);
        BigDecimal parsed = new BigDecimal(cleanString)
                .setScale(2, BigDecimal.ROUND_FLOOR)
                .divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance().format(parsed);
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}
