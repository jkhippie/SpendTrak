package com.danasoft.spendtrak.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.danasoft.spendtrak.model.Transaction;

@Database(entities = {Transaction.class}, version = 1)
public abstract class TransactionsDatabase extends RoomDatabase {
    private static TransactionsDatabase INSTANCE;
    private static final String TRANSACTIONS_DB_NAME = "transactions.db";

    public abstract TransactionDao transactionDao();

    public static TransactionsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TransactionsDatabase.class) {
                INSTANCE =
                        Room.databaseBuilder(
                                context.getApplicationContext(),
                                TransactionsDatabase.class,
                                TRANSACTIONS_DB_NAME)
                                .allowMainThreadQueries()
                                .fallbackToDestructiveMigration()
                                .build();
            }
        }
        return INSTANCE;
    }

//    public static void destroyInstance() {
//        INSTANCE = null;
//    }
}