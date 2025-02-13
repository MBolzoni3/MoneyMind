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
import it.unimib.devtrinity.moneymind.data.local.dao.BudgetDao;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class BudgetRepository extends GenericRepository {

    private static final String TAG = BudgetRepository.class.getSimpleName();

    private final BudgetDao budgetDao;

    public BudgetRepository(Application application) {
        super(application, Constants.BUDGETS_LAST_SYNC_KEY, TAG);
        this.budgetDao = DatabaseClient.getInstance(application).budgetDao();
    }

    public LiveData<List<BudgetEntityWithCategory>> getAll() {
        return budgetDao.getAll();
    }

    public void insertBudget(BudgetEntity budget, GenericCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                budgetDao.insertOrUpdate(budget);
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(true));
            } catch (Exception e) {
                Log.e(TAG, "Error inserting budget: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    public void delete(List<BudgetEntityWithCategory> budgets) {
        executorService.execute(() -> {
            for (BudgetEntityWithCategory budget : budgets) {
                budgetDao.deleteById(budget.getBudget().getId());
            }
        });
    }

    @Override
    protected CompletableFuture<Long> syncLocalToRemoteAsync() {
        return CompletableFuture.supplyAsync(() -> {
                    List<BudgetEntity> unsyncedBudgets = budgetDao.getUnsyncedBudgets();
                    List<CompletableFuture<Void>> syncFutures = new ArrayList<>();

                    for (BudgetEntity budget : unsyncedBudgets) {
                        budget.setSynced(true);

                        String documentId = budget.getFirestoreId();
                        DocumentReference docRef;

                        if (documentId == null || documentId.isEmpty()) {
                            docRef = FirestoreHelper.getInstance().getUserCollection(Constants.BUDGETS_COLLECTION_NAME).document();
                            budget.setFirestoreId(docRef.getId());
                        } else {
                            docRef = FirestoreHelper.getInstance().getUserCollection(Constants.BUDGETS_COLLECTION_NAME).document(documentId);
                        }

                        CompletableFuture<Void> future = runFirestoreSet(docRef, budget)
                                .thenRunAsync(() -> budgetDao.setSynced(budget.getId()), executorService);

                        syncFutures.add(future);
                    }

                    return syncFutures;
                }, executorService)
                .thenCompose(syncFutures -> CompletableFuture.allOf(syncFutures.toArray(new CompletableFuture[0])))
                .thenApply(v -> budgetDao.getLastSyncedTimestamp())
                .exceptionally(e -> {
                    Log.e(TAG, "Sync failed", e);
                    return 0L;
                });
    }

    private CompletableFuture<Void> runFirestoreSet(DocumentReference docRef, BudgetEntity budget) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        docRef.set(budget)
                .addOnSuccessListener(executorService, aVoid -> future.complete(null))
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error syncing budget to remote", e)));

        return future;
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return runFirestoreQuery(lastSyncedTimestamp)
                .thenAcceptAsync(querySnapshot -> {
                    for (BudgetEntity remoteBudget : querySnapshot.toObjects(BudgetEntity.class)) {
                        BudgetEntity localBudget = budgetDao.getByFirestoreId(remoteBudget.getFirestoreId());
                        if (localBudget == null) {
                            budgetDao.insertOrUpdate(remoteBudget);
                        } else {
                            BudgetEntity resolvedBudget = resolveConflict(localBudget, remoteBudget);
                            budgetDao.insertOrUpdate(resolvedBudget);
                        }
                    }
                }, executorService)
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing budgets from remote", e);
                    return null;
                });
    }

    private CompletableFuture<QuerySnapshot> runFirestoreQuery(long lastSyncedTimestamp) {
        CompletableFuture<QuerySnapshot> future = new CompletableFuture<>();

        FirestoreHelper.getInstance().getUserCollection(Constants.BUDGETS_COLLECTION_NAME)
                .whereGreaterThan("lastSyncedAt", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(executorService, future::complete)
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error fetching budgets from remote", e)));

        return future;
    }

    private BudgetEntity resolveConflict(BudgetEntity local, BudgetEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
