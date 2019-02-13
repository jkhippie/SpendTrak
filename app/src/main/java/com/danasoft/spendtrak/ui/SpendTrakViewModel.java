package com.danasoft.spendtrak.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.danasoft.spendtrak.database.SpendTrakDao;
import com.danasoft.spendtrak.database.SpendTrakDatabase;
import com.danasoft.spendtrak.model.Merchant;
import com.danasoft.spendtrak.model.Transaction;

import java.util.List;
import java.util.Objects;

//import android.arch.core.util.Function;

public class SpendTrakViewModel extends AndroidViewModel {
    private final SpendTrakDao dao;

    public SpendTrakViewModel(@NonNull Application application) {
        super(application);
        dao = SpendTrakDatabase.getDatabase(application).spendtrakDao();
    }

    List<Transaction> getTransactionList() { return dao.allTransactionsAsList(); }
    LiveData<List<Transaction>> getAllTransactions() { return dao.allTransactionsAsLiveData();}
    long insertTransaction(Transaction newTransaction) {
        return dao.insertTransaction(newTransaction);
    }
    public void removeTransaction(Transaction toRemove) { dao.removeTransaction(toRemove); }
    public void updateTransaction(Transaction toUpdate) { dao.updateTransaction(toUpdate); }
    List<Merchant> getMerchantList() {
        List<Merchant> merchants = dao.allMerchantsAsList();
        for (Merchant merchant : Objects.requireNonNull(merchants)) {
            merchant.setTransactions(dao.getTransactionsByMerchant(merchant.getMerchantId()));
        }
        return merchants;
    }
    void saveMerchant(Merchant merchant) {
        dao.insertMerchant(merchant);
        dao.insertTransactions(merchant.getTransactions());
    }
    void hideKeyboardFrom(View v) {
        ((InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}