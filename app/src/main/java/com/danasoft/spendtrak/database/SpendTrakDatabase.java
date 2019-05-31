package com.danasoft.spendtrak.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.danasoft.spendtrak.model.Merchant;
import com.danasoft.spendtrak.model.Transaction;


@Database(entities = {Transaction.class, Merchant.class}, version = 1)
public abstract class SpendTrakDatabase extends RoomDatabase {
    private static SpendTrakDatabase INSTANCE;
    private static final String SPENDTRAK_DB = "spendtrak.db";

    public abstract SpendTrakDao spendtrakDao();
    public static SpendTrakDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SpendTrakDatabase.class) {
                INSTANCE =
                        Room.databaseBuilder(
                                context.getApplicationContext(),
                                SpendTrakDatabase.class,
                                SPENDTRAK_DB)
                                .allowMainThreadQueries()
                                .fallbackToDestructiveMigration()
                                .build();
            }
        }
        return INSTANCE;
    }
}