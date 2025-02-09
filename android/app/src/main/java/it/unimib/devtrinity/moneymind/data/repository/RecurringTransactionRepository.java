package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.RecurringTransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class RecurringTransactionRepository extends GenericRepository {
    private static final String TAG = RecurringTransactionRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "recurring_transactions";

    private final RecurringTransactionDao recurringTransactionDao;

    public RecurringTransactionRepository(Application application) {
        super(application, Constants.RECURRING_TRANSACTIONS_LAST_SYNC_KEY, TAG);
        this.recurringTransactionDao = DatabaseClient.getInstance(application).recurringTransactionDao();
    }

    public LiveData<List<TransactionEntityWithCategory>> getRecurringTransactions() {
        return Transformations.map(recurringTransactionDao.getAll(), recurringTransactions -> {
            List<TransactionEntityWithCategory> recurringTransactionsAsTransactions = new ArrayList<>();
            for (RecurringTransactionEntityWithCategory recurringTransaction : recurringTransactions) {
                TransactionEntityWithCategory transaction = new TransactionEntityWithCategory();
                transaction.setTransaction(recurringTransaction.getTransaction());
                transaction.setCategory(recurringTransaction.getCategory());

                recurringTransactionsAsTransactions.add(transaction);
            }

            return recurringTransactionsAsTransactions;
        });
    }

    public void delete(List<RecurringTransactionEntity> transactions) {
        executorService.execute(() -> {
            for (RecurringTransactionEntity transaction : transactions) {
                recurringTransactionDao.deleteById(transaction.getId());
            }
        });
    }

    public void insertTransaction(RecurringTransactionEntity transaction, GenericCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                recurringTransactionDao.insertOrUpdate(transaction);
                callback.onSuccess(true);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting recurring transaction: " + e.getMessage(), e);
                callback.onFailure(e.getMessage());
            }
        });
    }

    @Override
    protected CompletableFuture<Void> syncLocalToRemoteAsync() {
        return CompletableFuture.runAsync(() -> {
            List<RecurringTransactionEntity> unsyncedRecurringTransactions = recurringTransactionDao.getUnsyncedTransactions();

            for (RecurringTransactionEntity recurringTransaction : unsyncedRecurringTransactions) {
                recurringTransaction.setSynced(true);

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
                            throw new RuntimeException("Error syncing Recurring Transaction to remote: " + e.getMessage(), e);
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
                        throw new RuntimeException("Error syncing recurring transactions from remote: " + e.getMessage(), e);
                    });
        }, executorService);
    }

    private RecurringTransactionEntity resolveConflict(RecurringTransactionEntity local, RecurringTransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
