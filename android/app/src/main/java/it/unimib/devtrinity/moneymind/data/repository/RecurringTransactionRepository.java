package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

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

    public void insertRecurringTransaction(RecurringTransactionEntity recurringTransaction) {
        executorService.execute(() -> recurringTransactionDao.insertOrUpdate(recurringTransaction));
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
            String documentId = recurringTransaction.getFirestoreId();
            DocumentReference docRef;

            if (documentId == null || documentId.isEmpty()) {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                recurringTransaction.setFirestoreId(docRef.getId());
            } else {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
            }

            docRef.set(recurringTransaction)
                    .addOnSuccessListener(aVoid -> {
                        recurringTransaction.setSynced(true);
                        executorService.execute(() -> recurringTransactionDao.insertOrUpdate(recurringTransaction));

                        Log.d(TAG, "Recurring Transaction synced to remote: " + recurringTransaction.getFirestoreId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing Recurring Transaction to remote: " + e.getMessage(), e);
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
                            executorService.execute(() -> recurringTransactionDao.insertOrUpdate(remoteRecurringTransaction));
                        } else {
                            RecurringTransactionEntity resolvedTransaction = resolveConflict(localRecurringTransaction, remoteRecurringTransaction);
                            executorService.execute(() -> recurringTransactionDao.insertOrUpdate(resolvedTransaction));
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
