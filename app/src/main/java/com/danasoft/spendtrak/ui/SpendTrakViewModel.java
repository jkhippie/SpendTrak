package com.danasoft.spendtrak.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.danasoft.spendtrak.database.TransactionDao;
import com.danasoft.spendtrak.database.TransactionsDatabase;
import com.danasoft.spendtrak.model.Transaction;

import java.util.List;

public class SpendTrakViewModel extends AndroidViewModel {
    //private Application mApplication;
    private TransactionDao dao;

    public SpendTrakViewModel(@NonNull Application application) {
        super(application);
        dao = TransactionsDatabase.getDatabase(application).transactionDao();
        //mApplication = application;
    }

    List<Transaction> getTransctionList() { return dao.allTransactionsAsList(); }
    LiveData<List<Transaction>> getAllTransactions() { return dao.allTransactionsAsLiveData();}
    long addTransaction(Transaction newTransaction) {
        long count = dao.transCount();
        if (count == 1) {
            Transaction t = dao.allTransactionsAsList().get(0);
            if (t.getTransactionMerchant().equals(Transaction.NEW_TRANS)) {
                dao.remove(t);
            }
        }
        return dao.insert(newTransaction);
    }
    void removeTransaction(Transaction toRemove) { dao.remove(toRemove); }
    void updateTransaction(Transaction toUpdate) { dao.update(toUpdate); }
}