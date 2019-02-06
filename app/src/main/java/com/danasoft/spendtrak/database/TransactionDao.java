package com.danasoft.spendtrak.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.danasoft.spendtrak.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY t_id DESC")
    LiveData<List<Transaction>> allTransactionsAsLiveData();

    @Query("SELECT * FROM transactions ORDER BY t_id ASC")
    List<Transaction> allTransactionsAsList();

    @Query("SELECT * FROM transactions WHERE t_merchant = :merchant LIMIT 1")
    Transaction findTransactionByMerchant(String merchant);

    @Query("SELECT * FROM transactions WHERE t_amount = :amount LIMIT 1")
    Transaction findTransactionByAmount(double amount);

//    @Query("SELECT * FROM transactions WHERE t_date = :date LIMIT 1")
//    Transaction findTransactionByDate(String date);
//
//    @Query("SELECT * FROM transactions WHERE t_date LIKE :guess")
//    Transaction findTransactionFuzzyDate(String guess);

    @Query("SELECT * FROM transactions WHERE t_merchant LIKE :guess")
    Transaction findTransactionFuzzyMerchant(String guess);

    @Query("SELECT * FROM transactions WHERE t_amount LIKE :guess")
    Transaction findTransactionFuzzyAmount(String guess);

//    @Query("SELECT * FROM transactions WHERE transactionTimeStamp LIKE :guess")
//    Transaction findTransactionFuzzyTime(String guess);

    @Query("SELECT COUNT(*) FROM transactions")
    long transCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Transaction dest);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(Transaction... dests);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Transaction dest);

    @Delete
    void remove(Transaction dest);
}
