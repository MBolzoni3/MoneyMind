package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.RecurringTransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class RecurringTransactionRepository extends GenericRepository {
    private static final String TAG = RecurringTransactionRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "recurring_transactions";

    private final RecurringTransactionDao recurringTransactionDao;
    private final SharedPreferences sharedPreferences;

    public RecurringTransactionRepository(Context context) {
        this.recurringTransactionDao = DatabaseClient.getInstance(context).recurringTransactionDao();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(context);
    }

    public void syncRecurringTransactionsAsyc() {
        executorService.execute(this::syncRecurringTransactions);
    }

    public void syncRecurringTransactions() {
        long lastSyncedTimestamp = sharedPreferences.getLong(Constants.RECURRING_TRANSACTIONS_LAST_SYNC_KEY, 0);

        syncLocalToRemote();
        syncRemoteToLocal(lastSyncedTimestamp);

        sharedPreferences.edit().putLong(Constants.RECURRING_TRANSACTIONS_LAST_SYNC_KEY, System.currentTimeMillis()).apply();
    }

    private void syncLocalToRemote() {
        List<RecurringTransactionEntity> unsyncedRecurringTransactions = recurringTransactionDao.getUnsyncedTransactions();

        for (RecurringTransactionEntity recurringTransaction : unsyncedRecurringTransactions) {
            FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                    .document(recurringTransaction.getFirestoreId())
                    .set(recurringTransaction)
                    .addOnSuccessListener(aVoid -> {
                        recurringTransaction.setSynced(true);
                        recurringTransactionDao.insertOrUpdate(recurringTransaction);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing recurring transaction to remote: " + e.getMessage(), e);
                    });
        }
    }

    private void syncRemoteToLocal(long lastSyncedTimestamp) {
        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("updated_at", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (RecurringTransactionEntity remoteRecurringTransaction : querySnapshot.toObjects(RecurringTransactionEntity.class)) {
                        RecurringTransactionEntity localRecurringTransaction = recurringTransactionDao.getByFirestoreId(remoteRecurringTransaction.getFirestoreId());

                        if (localRecurringTransaction == null) {
                            recurringTransactionDao.insertOrUpdate(remoteRecurringTransaction);
                        } else {
                            RecurringTransactionEntity resolvedTransaction = resolveConflict(localRecurringTransaction, remoteRecurringTransaction);
                            recurringTransactionDao.insertOrUpdate(resolvedTransaction);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing recurring transactions from remote: " + e.getMessage(), e);
                });
    }

    private RecurringTransactionEntity resolveConflict(RecurringTransactionEntity local, RecurringTransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
