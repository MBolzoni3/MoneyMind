package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public RecurringTransactionRepository(Context context) {
        super(context, Constants.RECURRING_TRANSACTIONS_LAST_SYNC_KEY, TAG);
        this.recurringTransactionDao = DatabaseClient.getInstance(context).recurringTransactionDao();
    }

    @Override
    protected CompletableFuture<Void> syncLocalToRemoteAsync() {
        return CompletableFuture.runAsync(() -> {
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
                        .addOnSuccessListener(executorService, aVoid -> {
                            recurringTransactionDao.setSynced(recurringTransaction.getId());

                            Log.d(TAG, "Recurring Transaction synced to remote: " + recurringTransaction.getFirestoreId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error syncing Recurring Transaction to remote: " + e.getMessage(), e);
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
        }, executorService);
    }

    private RecurringTransactionEntity resolveConflict(RecurringTransactionEntity local, RecurringTransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
