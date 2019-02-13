package com.danasoft.spendtrak.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "transactions", indices = {@Index(value = "t_id", unique = true)})
public class Transaction implements Serializable {
    public static final String NEW_TRANS = "No transactions";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "t_id")          private long transactionId;
    @ColumnInfo(name = "t_time")        private long transactionTimeStamp;
    @ColumnInfo(name = "t_amount")      private double transactionAmount;
    @ColumnInfo(name = "t_notes")       private String transactionNotes;
    @ColumnInfo(name = "t_merchantId")  private long transactionMerchantId;
    @Ignore                             private Object tag;
    @Ignore                             private int tmpPos;

    public Transaction() {}

    public Transaction(int _id, long _timeStamp, int _merchantId, double _amount, String _notes) {
        transactionId = _id;
        transactionTimeStamp = _timeStamp;
        transactionMerchantId = _merchantId;
        transactionAmount = _amount;
        transactionNotes = _notes;
    }

    @Ignore
    public Transaction(long _timeStamp, long _merchantId, double _amount, String _notes) {
        transactionTimeStamp = _timeStamp;
        transactionMerchantId = _merchantId;
        transactionAmount = _amount;
        transactionNotes = _notes;
    }

    @Ignore
    @NotNull
    @Contract("_ -> new")
    public static Transaction getNew(int merchantId) {
        return new Transaction(System.currentTimeMillis(), merchantId,0.0d,"No notes.");
    }
    public long getTransactionId()                      { return transactionId; }
    public void setTransactionId(long tId)              { this.transactionId = tId; }
    public long getTransactionTimeStamp()               { return transactionTimeStamp; }
    public void setTransactionTimeStamp(long mTime)     { this.transactionTimeStamp = mTime; }
    public long getTransactionMerchantId()              { return transactionMerchantId; }
    public void setTransactionMerchantId(long _id)      { this.transactionMerchantId = _id; }
    public double getTransactionAmount()                { return transactionAmount; }
    public void setTransactionAmount(double mAmount)    { this.transactionAmount = mAmount; }
    public String getTransactionNotes()                 { return transactionNotes; }
    public void setTransactionNotes(String notes)       { transactionNotes = notes; }
    public Object getTag()                              { return tag; }
    public void setTag(Object tag)                      { this.tag = tag; }

    public int getTmpPos() {
        return tmpPos;
    }

    public void setTmpPos(int tmpPos) {
        this.tmpPos = tmpPos;
    }
}