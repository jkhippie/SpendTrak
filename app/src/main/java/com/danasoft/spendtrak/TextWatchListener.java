package com.danasoft.spendtrak;

import android.support.annotation.IdRes;

public interface TextWatchListener {
    void hasData(@IdRes int resId, boolean has);
}
