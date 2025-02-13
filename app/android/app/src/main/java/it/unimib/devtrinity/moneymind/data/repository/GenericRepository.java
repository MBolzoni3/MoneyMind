package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;

public abstract class GenericRepository {
    protected final ExecutorService executorService;
    protected final SharedPreferences sharedPreferences;
    private final String SYNC_KEY;
    private final String TAG;

    public GenericRepository(Application application, String SYNC_KEY, String TAG) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(application);
        this.SYNC_KEY = SYNC_KEY;
        this.TAG = TAG;
    }

    public CompletableFuture<Void> sync() {
        return sync(sharedPreferences.getLong(SYNC_KEY, 0));
    }

    public CompletableFuture<Void> sync(long lastSyncedTimestamp) {
        return CompletableFuture.supplyAsync(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Void> future = executor.submit(() -> {
                syncLocalToRemoteAsync().get();
                return null;
            });

            try {
                future.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new RuntimeException("Timeout during syncLocalToRemoteAsync", e);
            } catch (Exception e) {
                throw new RuntimeException("Error during sync", e);
            } finally {
                executor.shutdown();
            }

            return null;
        }).thenCompose(v -> CompletableFuture.supplyAsync(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Void> future = executor.submit(() -> {
                syncRemoteToLocalAsync(lastSyncedTimestamp).get();
                return null;
            });

            try {
                future.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new RuntimeException("Timeout during syncRemoteToLocalAsync", e);
            } catch (Exception e) {
                throw new RuntimeException("Error during sync", e);
            } finally {
                executor.shutdown();
            }

            return null;
        }));
    }

    /*public CompletableFuture<Void> sync(long lastSyncedTimestamp) {
        return syncLocalToRemoteAsync()
                .thenCompose(lastTimestampRoom -> {
                    long finalLastSyncedTimestamp = resolveLastSyncedTimestamp(lastTimestampRoom, lastSyncedTimestamp);
                    return syncRemoteToLocalAsync(finalLastSyncedTimestamp)
                            .thenRun(() -> {
                                sharedPreferences.edit().putLong(SYNC_KEY, finalLastSyncedTimestamp).apply();
                            });
                })
                .whenComplete((v, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error syncing: " + e.getMessage(), e);
                    }
                });
    }*/

    protected CompletableFuture<Long> syncLocalToRemoteAsync() {
        return CompletableFuture.completedFuture(null);
    }

    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return CompletableFuture.completedFuture(null);
    }

    private long resolveLastSyncedTimestamp(Long roomTimestamp, long sharedTimestamp) {
        if (roomTimestamp == null || roomTimestamp == 0L) {
            return sharedTimestamp;
        }

        if (sharedTimestamp == 0L) {
            return roomTimestamp;
        }
        return Math.max(roomTimestamp, sharedTimestamp);
    }

}