package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;

public abstract class GenericRepository {
    protected final ExecutorService executorService;
    protected final SharedPreferences sharedPreferences;
    private final String SYNC_KEY;
    private final String TAG;

    public GenericRepository(Context context, String SYNC_KEY, String TAG) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(context);
        this.SYNC_KEY = SYNC_KEY;
        this.TAG = TAG;
    }

    public void sync() {
        sync(sharedPreferences.getLong(SYNC_KEY, 0));
    }

    public void sync(long lastSyncedTimestamp) {
        CompletableFuture.runAsync(() -> {
            try {
                syncLocalToRemoteAsync()
                        .thenCompose(v -> syncRemoteToLocalAsync(lastSyncedTimestamp))
                        .thenRun(() -> sharedPreferences.edit().putLong(SYNC_KEY, System.currentTimeMillis()).apply())
                        .exceptionally(e -> {
                            Log.e(TAG, "Error syncing: " + e.getMessage(), e);
                            return null;
                        });
            } catch (Exception e) {
                Log.e(TAG, "Error syncing: " + e.getMessage(), e);
            }
        }, executorService);
    }

    protected CompletableFuture<Void> syncLocalToRemoteAsync() {
        return CompletableFuture.runAsync(() -> {
        }, executorService);
    }

    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return CompletableFuture.runAsync(() -> {
        }, executorService);
    }
}