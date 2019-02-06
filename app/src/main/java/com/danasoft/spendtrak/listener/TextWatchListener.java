package com.danasoft.spendtrak.listener;

import android.support.annotation.IdRes;

public interface TextWatchListener {
    void hasData(@IdRes int resId, boolean has);
}
