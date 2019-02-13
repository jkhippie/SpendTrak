package com.danasoft.spendtrak.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "merchants", indices = {@Index(value = "m_name", unique = true)})
public class Merchant  implements Serializable {

    public Merchant(){}

    public Merchant(long id, String name) {
        merchantId = id;
        merchantName = name;
        mTransactions = new ArrayList<>();
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "m_id")                          private long merchantId;
    @ColumnInfo(name = "m_name")                        private String merchantName;
    @Ignore                                             private List<Transaction> mTransactions;

    public long getMerchantId()                         { return merchantId; }
    public void setMerchantId(long merchantId)          { this.merchantId = merchantId; }
    public String getMerchantName()                     { return merchantName; }
    public void setMerchantName(String merchantName)    { this.merchantName = merchantName; }
    public List<Transaction> getTransactions()          { return mTransactions; }
    public int getCount()                               { return mTransactions.size(); }
    public void setTransactions(List<Transaction> mTransactions) {
        this.mTransactions = mTransactions;
    }
    public double getTransactionTotal() {
        double retVal = 0d;
        for (Transaction t : mTransactions) {
            retVal += t.getTransactionAmount();
        }
        return retVal;
    }
}