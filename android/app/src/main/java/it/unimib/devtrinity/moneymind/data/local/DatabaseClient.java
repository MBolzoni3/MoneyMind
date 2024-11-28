package it.unimib.devtrinity.moneymind.data.local;

import android.content.Context;

import androidx.room.Room;

import it.unimib.devtrinity.moneymind.constant.Constants;

public class DatabaseClient {

    private static AppDatabase instance;

    private DatabaseClient() {
        // Private constructor to prevent instantiation
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            Constants.DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
