package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class TransactionRepository extends GenericRepository {
    private static final String TAG = TransactionRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "transactions";

    private final TransactionDao transactionDao;
    private final SharedPreferences sharedPreferences;

    public TransactionRepository(Context context) {
        this.transactionDao = DatabaseClient.getInstance(context).transactionDao();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(context);
    }

    public void insertTransaction(TransactionEntity transaction) {
        executorService.execute(() -> transactionDao.insertOrUpdate(transaction));
    }

    public LiveData<Long> getSpentAmount(int categoryId, long startDate, long endDate) {
        return transactionDao.getSumForCategoryAndDateRange(categoryId, startDate, endDate);
    }

    public void getPositiveTransactions(GenericCallback<List<TransactionEntity>> callback) {
        callback.onSuccess(transactionDao.selectPositiveTransactions());
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
            String documentId = transaction.getFirestoreId();
            DocumentReference docRef;

            if (documentId == null || documentId.isEmpty()) {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                transaction.setFirestoreId(docRef.getId());
            } else {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
            }

            docRef.set(transaction)
                    .addOnSuccessListener(aVoid -> {
                        transaction.setSynced(true);
                        executorService.execute(() -> transactionDao.insertOrUpdate(transaction));

                        Log.d(TAG, "Transaction synced to remote: " + transaction.getFirestoreId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing Transaction to remote: " + e.getMessage(), e);
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
                            executorService.execute(() -> transactionDao.insertOrUpdate(remoteTransaction));
                        } else {
                            TransactionEntity resolvedTransaction = resolveConflict(localTransaction, remoteTransaction);
                            executorService.execute(() -> transactionDao.insertOrUpdate(resolvedTransaction));
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
