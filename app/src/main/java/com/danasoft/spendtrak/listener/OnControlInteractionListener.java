package com.danasoft.spendtrak.listener;

import android.support.annotation.Nullable;
import com.danasoft.spendtrak.model.Transaction;
import java.util.List;

public interface OnControlInteractionListener {
    void onControlInteraction(String key, @Nullable List< Transaction > data);
}
