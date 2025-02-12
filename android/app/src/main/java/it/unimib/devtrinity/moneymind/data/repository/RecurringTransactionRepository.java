package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

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
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(true));
            } catch (Exception e) {
                Log.e(TAG, "Error inserting recurring transaction: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    @Override
    protected CompletableFuture<Long> syncLocalToRemoteAsync() {
        return CompletableFuture.supplyAsync(() -> {
                    List<RecurringTransactionEntity> unsyncedRecurringTransactions = recurringTransactionDao.getUnsyncedTransactions();
                    List<CompletableFuture<Void>> syncFutures = new ArrayList<>();

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

                        CompletableFuture<Void> future = runFirestoreSet(docRef, recurringTransaction)
                                .thenRunAsync(() -> recurringTransactionDao.setSynced(recurringTransaction.getId()), executorService);

                        syncFutures.add(future);
                    }

                    return syncFutures;
                }, executorService)
                .thenCompose(syncFutures -> CompletableFuture.allOf(syncFutures.toArray(new CompletableFuture[0])))
                .thenApply(v -> recurringTransactionDao.getLastSyncedTimestamp())
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing recurring transactions to remote", e);
                    return 0L;
                });
    }

    private CompletableFuture<Void> runFirestoreSet(DocumentReference docRef, RecurringTransactionEntity transaction) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        docRef.set(transaction)
                .addOnSuccessListener(executorService, aVoid -> future.complete(null))
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error syncing recurring transaction to remote", e)));

        return future;
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return runFirestoreQuery(lastSyncedTimestamp)
                .thenAcceptAsync(querySnapshot -> {
                    for (RecurringTransactionEntity remoteRecurringTransaction : querySnapshot.toObjects(RecurringTransactionEntity.class)) {
                        RecurringTransactionEntity localRecurringTransaction = recurringTransactionDao.getByFirestoreId(remoteRecurringTransaction.getFirestoreId());
                        if (localRecurringTransaction == null) {
                            recurringTransactionDao.insertOrUpdate(remoteRecurringTransaction);
                        } else {
                            RecurringTransactionEntity resolvedTransaction = resolveConflict(localRecurringTransaction, remoteRecurringTransaction);
                            recurringTransactionDao.insertOrUpdate(resolvedTransaction);
                        }
                    }
                }, executorService)
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing recurring transactions from remote", e);
                    return null;
                });
    }

    private CompletableFuture<QuerySnapshot> runFirestoreQuery(long lastSyncedTimestamp) {
        CompletableFuture<QuerySnapshot> future = new CompletableFuture<>();

        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("lastSyncedAt", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(executorService, future::complete)
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error fetching recurring transactions from remote", e)));

        return future;
    }

    private RecurringTransactionEntity resolveConflict(RecurringTransactionEntity local, RecurringTransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
