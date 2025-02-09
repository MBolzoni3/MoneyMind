package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;

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
    private static final String COLLECTION_NAME = "budgets";

    private final BudgetDao budgetDao;

    public BudgetRepository(Context context) {
        super(context, Constants.BUDGETS_LAST_SYNC_KEY, TAG);
        this.budgetDao = DatabaseClient.getInstance(context).budgetDao();
    }

    public LiveData<List<BudgetEntityWithCategory>> getAll() {
        return budgetDao.getAll();
    }

    public void insertBudget(BudgetEntity budget, GenericCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                budgetDao.insertOrUpdate(budget);
                callback.onSuccess(true);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting budget: " + e.getMessage(), e);
                callback.onFailure(e.getMessage());
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
    protected CompletableFuture<Void> syncLocalToRemoteAsync() {
        return CompletableFuture.runAsync(() -> {
            List<BudgetEntity> unsyncedBudgets = budgetDao.getUnsyncedBudgets();

            for (BudgetEntity budget : unsyncedBudgets) {
                budget.setSynced(true);

                String documentId = budget.getFirestoreId();
                DocumentReference docRef;

                if (documentId == null || documentId.isEmpty()) {
                    docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                    budget.setFirestoreId(docRef.getId());
                } else {
                    docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
                }

                docRef.set(budget)
                        .addOnSuccessListener(executorService, aVoid -> {
                            budgetDao.setSynced(budget.getId());

                            Log.d(TAG, "Budget synced to remote: " + budget.getFirestoreId());
                        })
                        .addOnFailureListener(e -> {
                            throw new RuntimeException("Error syncing budget to remote: " + e.getMessage(), e);
                        });
            }
        }, executorService);
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return CompletableFuture.runAsync(() -> {
            FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                    .whereGreaterThan("lastSyncedAt", lastSyncedTimestamp)
                    .get()
                    .addOnSuccessListener(executorService, querySnapshot -> {
                        for (BudgetEntity remoteBudget : querySnapshot.toObjects(BudgetEntity.class)) {
                            BudgetEntity localBudget = budgetDao.getByFirestoreId(remoteBudget.getFirestoreId());
                            if (localBudget == null) {
                                budgetDao.insertOrUpdate(remoteBudget);
                            } else {
                                BudgetEntity resolvedBudget = resolveConflict(localBudget, remoteBudget);
                                budgetDao.insertOrUpdate(resolvedBudget);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        throw new RuntimeException("Error syncing budgets from remote: " + e.getMessage(), e);
                    });
        }, executorService);
    }

    private BudgetEntity resolveConflict(BudgetEntity local, BudgetEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
