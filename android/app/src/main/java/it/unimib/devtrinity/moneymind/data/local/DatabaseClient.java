package it.unimib.devtrinity.moneymind.data.local;

import android.app.Application;

import androidx.room.Room;

import it.unimib.devtrinity.moneymind.constant.Constants;

public class DatabaseClient {

    private static AppDatabase instance;

    private DatabaseClient() {
    }

    public static synchronized AppDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            application.getApplicationContext(),
                            AppDatabase.class,
                            Constants.DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
