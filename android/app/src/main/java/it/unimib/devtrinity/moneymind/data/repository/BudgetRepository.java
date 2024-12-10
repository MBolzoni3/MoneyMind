package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.BudgetDao;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class BudgetRepository extends GenericRepository {

    private static final String TAG = BudgetRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "budgets";

    private final BudgetDao budgetDao;
    private final SharedPreferences sharedPreferences;

    public BudgetRepository(Context context) {
        this.budgetDao = DatabaseClient.getInstance(context).budgetDao();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(context);
    }

    public void syncBudgets() {
        long lastSyncedTimestamp = sharedPreferences.getLong(Constants.BUDGETS_LAST_SYNC_KEY, 0);

        syncLocalToRemote();
        syncRemoteToLocal(lastSyncedTimestamp);

        sharedPreferences.edit().putLong(Constants.BUDGETS_LAST_SYNC_KEY, System.currentTimeMillis()).apply();
    }

    private void syncLocalToRemote() {
        List<BudgetEntity> unsyncedBudgets = budgetDao.getUnsyncedBudgets();

        for (BudgetEntity budget : unsyncedBudgets) {
            String documentId = budget.getFirestoreId();
            DocumentReference docRef;

            if (documentId == null || documentId.isEmpty()) {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                budget.setFirestoreId(docRef.getId());
            } else {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
            }

            docRef.set(budget)
                    .addOnSuccessListener(aVoid -> {
                        budget.setSynced(true);
                        executorService.execute(() -> budgetDao.insertOrUpdate(budget));

                        Log.d(TAG, "Budget synced to remote: " + budget.getFirestoreId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing budget to remote: " + e.getMessage(), e);
                    });
        }
    }

    private void syncRemoteToLocal(long lastSyncedTimestamp) {
        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("updated_at", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (BudgetEntity remoteBudget : querySnapshot.toObjects(BudgetEntity.class)) {
                        BudgetEntity localBudget = budgetDao.getByFirestoreId(remoteBudget.getFirestoreId());

                        if (localBudget == null) {
                            executorService.execute(() -> budgetDao.insertOrUpdate(remoteBudget));
                        } else {
                            BudgetEntity resolvedBudget = resolveConflict(localBudget, remoteBudget);
                            executorService.execute(() -> budgetDao.insertOrUpdate(resolvedBudget));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing budgets from remote: " + e.getMessage(), e);
                });
    }

    private BudgetEntity resolveConflict(BudgetEntity local, BudgetEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
