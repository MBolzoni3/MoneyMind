package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteStatement;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.AppDatabase;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;

public class DatabaseRepository {
    private static final String TAG = CategoryRepository.class.getSimpleName();

    private final AppDatabase database;

    private final String[] defaultTablesToClear = {
            Constants.BUDGETS_TABLE_NAME,
            Constants.GOALS_TABLE_NAME,
            Constants.TRANSACTIONS_TABLE_NAME,
            Constants.RECURRING_TRANSACTIONS_TABLE_NAME,
    };

    public DatabaseRepository(Application application) {
        this.database = DatabaseClient.getInstance(application);
    }

    public CompletableFuture<Void> clearUserTables() {
        return clearTables(defaultTablesToClear);
    }

    public CompletableFuture<Void> clearTables(String... tableNames) {
        return CompletableFuture.runAsync(() -> {
            database.runInTransaction(() -> {
                for (String table : tableNames) {
                    try (SupportSQLiteStatement statement = database.compileStatement("DELETE FROM " + table)) {
                        statement.execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }).exceptionally(e -> {
            Log.e(TAG, "Error clearing tables: " + e.getMessage(), e);
            return null;
        });
    }
}
