package com.danasoft.spendtrak.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.danasoft.spendtrak.TextUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Entity(tableName = "transactions", indices = {@Index(value = "t_id", unique = true)})
public class Transaction {
    public static final String NEW_TRANS = "No transactions";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "t_id")
    private long transactionId;
    @ColumnInfo(name = "t_time")
    private String transactionTimeStamp;
    @ColumnInfo(name = "t_merchant")
    private String transactionMerchant;
    @ColumnInfo(name = "t_amount")
    private double transactionAmount;
    @ColumnInfo(name = "t_notes")
    private String transactionNotes;
    @Ignore private Object tag;

    public Transaction() {}

    public Transaction(int _id, String _time, String _merchant, double _amount, String _notes) {
        transactionId = _id;
        transactionTimeStamp = _time;
        transactionMerchant = _merchant;
        transactionAmount = _amount;
        transactionNotes = _notes;
    }

    @Ignore
    public Transaction(String _time, String _merchant, double _amount, String _notes) {
        transactionTimeStamp = _time;
        transactionMerchant = _merchant;
        transactionAmount = _amount;
        transactionNotes = _notes;
    }

    @Ignore
    @NotNull
    @Contract(" -> new")
    public static Transaction getNew() {
        return new Transaction(TextUtils.getTimeStamp(), NEW_TRANS, 0.0d,"No notes.");
    }

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long tId) { this.transactionId = tId; }
    public String getTransactionTimeStamp() { return transactionTimeStamp; }
    public void setTransactionTimeStamp(String mTime) { this.transactionTimeStamp = mTime; }
    public String getTransactionMerchant() { return transactionMerchant; }
    public void setTransactionMerchant(String mMerchant) { this.transactionMerchant = mMerchant; }
    public double getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(double mAmount) { this.transactionAmount = mAmount; }
    public String getTransactionNotes() { return transactionNotes; }
    public void setTransactionNotes(String notes) { transactionNotes = notes; }
    public Object getTag() { return tag; }
    public void setTag(Object tag) {this.tag = tag; }
}