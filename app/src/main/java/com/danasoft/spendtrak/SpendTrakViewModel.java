package com.danasoft.spendtrak;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import com.danasoft.spendtrak.database.TransactionDao;
import com.danasoft.spendtrak.database.TransactionsDatabase;
import com.danasoft.spendtrak.model.Transaction;

import java.util.Date;
import java.util.List;

public class SpendTrakViewModel extends AndroidViewModel {
    //private Application mApplication;
    private TransactionDao dao;

    public SpendTrakViewModel(@NonNull Application application) {
        super(application);
        dao = TransactionsDatabase.getDatabase(application).transactionDao();
        //mApplication = application;
    }

    public List<Transaction> getTransctionList() { return dao.allTransactionsAsList(); }
    public LiveData<List<Transaction>> getAllTransactions() { return dao.allTransactionsAsLiveData();}
    public long addTransaction(Transaction newTransaction) {
        long count = dao.transCount();
        if (count == 1) {
            Transaction t = dao.allTransactionsAsList().get(0);
            if (t.getTransactionMerchant().equals(Transaction.NEW_TRANS)) {
                dao.remove(t);
            }
        }
        return dao.insert(newTransaction);
    }
    public void removeTransaction(Transaction toRemove) {
        dao.remove(toRemove);
    }
    public void updateTransaction(Transaction toUpdate) { dao.update(toUpdate);}

    String getDate() {
        String[] dd = new Date().toString().split(" ");
        return dd[0] + dd[1] + dd[2];
    }
    String getTime() {
        String[] dd = new Date().toString().split(" ");
        return dd[3];
    }
}