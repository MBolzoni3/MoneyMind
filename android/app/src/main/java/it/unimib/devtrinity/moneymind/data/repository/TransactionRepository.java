package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class TransactionRepository extends GenericRepository {
    private static final String TAG = TransactionRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "transactions";

    private final TransactionDao transactionDao;

    public TransactionRepository(Context context) {
        super(context, Constants.TRANSACTIONS_LAST_SYNC_KEY, TAG);
        this.transactionDao = DatabaseClient.getInstance(context).transactionDao();
    }

    public LiveData<List<TransactionEntity>> getTransactions(int month) {

        return transactionDao.selectTransactions(month);
    }

    public LiveData<Long> getSpentAmount(String categoryId, long startDate, long endDate) {
        return transactionDao.getSumForCategoryAndDateRange(categoryId, startDate, endDate);
    }

    @Override
    protected CompletableFuture<Void> syncLocalToRemoteAsync() {
        return CompletableFuture.runAsync(() -> {
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
                        .addOnSuccessListener(executorService, aVoid -> {
                            transactionDao.setSynced(transaction.getId());

                            Log.d(TAG, "Transaction synced to remote: " + transaction.getFirestoreId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error syncing Transaction to remote: " + e.getMessage(), e);
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
        }, executorService);
    }

    private TransactionEntity resolveConflict(TransactionEntity local, TransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
