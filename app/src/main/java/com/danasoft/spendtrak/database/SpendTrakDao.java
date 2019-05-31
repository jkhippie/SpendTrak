package com.danasoft.spendtrak.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.danasoft.spendtrak.model.Merchant;
import com.danasoft.spendtrak.model.Transaction;

import java.util.List;

@Dao
public interface SpendTrakDao {
    @Query("SELECT * FROM transactions ORDER BY t_id DESC")
    LiveData<List<Transaction>>         allTransactionsAsLiveData();
    @Query("SELECT * FROM transactions ORDER BY t_id DESC")
    List<Transaction>                   allTransactionsAsList();
    @Query("SELECT * FROM transactions WHERE t_time = :ts LIMIT 1")
    Transaction                         findTransactionByTimestamp(long ts);
    @Query("SELECT * FROM merchants ORDER BY m_id DESC")
    LiveData<List<Merchant>>            allMerchantsAsLiveData();
    @Query("SELECT * FROM merchants ORDER BY m_id DESC")
    List<Merchant>                      allMerchantsAsList();
    @Query("SELECT * FROM merchants WHERE m_id = :id LIMIT 1")
    Merchant                            getMerchantById(long id);
    @Query("SELECT * FROM merchants")
    LiveData<List<Merchant>>            getMerchants();
    @Query("SELECT * FROM transactions WHERE t_merchantId = :merchantId")
    List<Transaction>                   getTransactionsByMerchant(long merchantId);
    @Query("SELECT * FROM transactions WHERE t_amount BETWEEN :minAmount AND :maxAmount")
    public List<Transaction> findTransactionsBetween(int minAmount, int maxAmount);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertTransaction(Transaction t);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransactions(List<Transaction> tList);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMerchant(Merchant merch);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMerchants(List<Merchant> mList);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTransaction(Transaction t);
    //@Update(onConflict = OnConflictStrategy.REPLACE)
    //void updateMerchant(Merchant merch);
    @Delete
    void removeTransaction(Transaction t);
    @Query("DELETE FROM transactions")
    void clearDb();
}