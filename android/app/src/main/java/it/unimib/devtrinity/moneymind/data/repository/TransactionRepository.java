package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class TransactionRepository extends GenericRepository {
    private static final String TAG = TransactionRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "transactions";

    private final TransactionDao transactionDao;
    private final SharedPreferences sharedPreferences;

    public TransactionRepository(Context context) {
        super();
        this.transactionDao = DatabaseClient.getInstance(context).transactionDao();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(context);
    }

    public void syncTransactionsAsyc() {
        executorService.execute(this::syncTransactions);
    }

    public void syncTransactions() {
        long lastSyncedTimestamp = sharedPreferences.getLong(Constants.TRANSACTIONS_LAST_SYNC_KEY, 0);

        syncLocalToRemote();
        syncRemoteToLocal(lastSyncedTimestamp);

        sharedPreferences.edit().putLong(Constants.TRANSACTIONS_LAST_SYNC_KEY, System.currentTimeMillis()).apply();
    }

    private void syncLocalToRemote() {
        List<TransactionEntity> unsyncedTransactions = transactionDao.getUnsyncedTransactions();

        for (TransactionEntity transaction : unsyncedTransactions) {
            FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                    .document(transaction.getFirestoreId())
                    .set(transaction)
                    .addOnSuccessListener(aVoid -> {
                        transaction.setSynced(true);
                        transactionDao.insertOrUpdate(transaction);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing transaction to remote: " + e.getMessage(), e);
                    });
        }
    }

    private void syncRemoteToLocal(long lastSyncedTimestamp) {
        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("updated_at", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (TransactionEntity remoteTransaction : querySnapshot.toObjects(TransactionEntity.class)) {
                        TransactionEntity localTransaction = transactionDao.getByFirestoreId(remoteTransaction.getFirestoreId());

                        if (localTransaction == null) {
                            transactionDao.insertOrUpdate(remoteTransaction);
                        } else {
                            TransactionEntity resolvedTransaction = resolveConflict(localTransaction, remoteTransaction);
                            transactionDao.insertOrUpdate(resolvedTransaction);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing transactions from remote: " + e.getMessage(), e);
                });
    }

    private TransactionEntity resolveConflict(TransactionEntity local, TransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
