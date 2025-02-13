package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.GoalDao;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntityWithCategory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class GoalRepository extends GenericRepository {
    private static final String TAG = GoalRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "goals";

    private final GoalDao goalDao;

    public GoalRepository(Application application) {
        super(application, Constants.GOALS_LAST_SYNC_KEY, TAG);
        this.goalDao = DatabaseClient.getInstance(application).goalDao();
    }

    public LiveData<List<GoalEntityWithCategory>> getAll() {
        return goalDao.getAll();
    }

    public void delete(List<GoalEntityWithCategory> goals) {
        executorService.execute(() -> {
            for (GoalEntityWithCategory goal : goals) {
                goalDao.deleteById(goal.getGoal().getId());
            }
        });
    }

    public void insertGoal(GoalEntity goal, GenericCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                goalDao.insertOrUpdate(goal);
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(true));
            } catch (Exception e) {
                Log.e(TAG, "Error inserting budget: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    @Override
    protected CompletableFuture<Long> syncLocalToRemoteAsync() {
        return CompletableFuture.supplyAsync(() -> {
                    List<GoalEntity> unsyncedGoals = goalDao.getUnsyncedGoals();
                    List<CompletableFuture<Void>> syncFutures = new ArrayList<>();

                    for (GoalEntity goal : unsyncedGoals) {
                        goal.setSynced(true);

                        String documentId = goal.getFirestoreId();
                        DocumentReference docRef;

                        if (documentId == null || documentId.isEmpty()) {
                            docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                            goal.setFirestoreId(docRef.getId());
                        } else {
                            docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
                        }

                        CompletableFuture<Void> future = runFirestoreSet(docRef, goal)
                                .thenRunAsync(() -> goalDao.setSynced(goal.getId()), executorService);

                        syncFutures.add(future);
                    }

                    return syncFutures;
                }, executorService)
                .thenCompose(syncFutures -> CompletableFuture.allOf(syncFutures.toArray(new CompletableFuture[0])))
                .thenApply(v -> goalDao.getLastSyncedTimestamp())
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing goals to remote", e);
                    return 0L;
                });
    }

    private CompletableFuture<Void> runFirestoreSet(DocumentReference docRef, GoalEntity goal) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        docRef.set(goal)
                .addOnSuccessListener(executorService, aVoid -> future.complete(null))
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error syncing goal to remote", e)));

        return future;
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return runFirestoreQuery(lastSyncedTimestamp)
                .thenAcceptAsync(querySnapshot -> {
                    for (GoalEntity remoteGoal : querySnapshot.toObjects(GoalEntity.class)) {
                        GoalEntity localGoal = goalDao.getByFirestoreId(remoteGoal.getFirestoreId());
                        if (localGoal == null) {
                            goalDao.insertOrUpdate(remoteGoal);
                        } else {
                            GoalEntity resolvedGoal = resolveConflict(localGoal, remoteGoal);
                            goalDao.insertOrUpdate(resolvedGoal);
                        }
                    }
                }, executorService)
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing goals from remote", e);
                    return null;
                });
    }

    private CompletableFuture<QuerySnapshot> runFirestoreQuery(long lastSyncedTimestamp) {
        CompletableFuture<QuerySnapshot> future = new CompletableFuture<>();

        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("lastSyncedAt", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(executorService, future::complete)
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error fetching goals from remote", e)));

        return future;
    }

    private GoalEntity resolveConflict(GoalEntity local, GoalEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
