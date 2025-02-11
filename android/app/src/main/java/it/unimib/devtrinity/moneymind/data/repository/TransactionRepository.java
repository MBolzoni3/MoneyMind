package it.unimib.devtrinity.moneymind.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class TransactionRepository extends GenericRepository {
    private static final String TAG = TransactionRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "transactions";

    private final TransactionDao transactionDao;

    public TransactionRepository(Application application) {
        super(application, Constants.TRANSACTIONS_LAST_SYNC_KEY, TAG);
        this.transactionDao = DatabaseClient.getInstance(application).transactionDao();
    }

    public LiveData<List<TransactionEntity>> getTransactions(long startDate, long endDate) {
        return transactionDao.selectTransactions(startDate, endDate);
    }

    public LiveData<Long> getOldestTransactionDate() {
        return transactionDao.getOldestTransactionDate();
    }

    public LiveData<Map<String, List<TransactionEntity>>> getTransactionsByMonth(int monthsBack) {
        long startDate = Utils.getStartDateFromMonthsBack(monthsBack);

        return Transformations.map(transactionDao.selectTransactionsFromDate(startDate), transactions -> {
            Map<String, List<TransactionEntity>> transactionsByMonth = new LinkedHashMap<>();

            Calendar currentCal = Calendar.getInstance();
            currentCal.set(Calendar.DAY_OF_MONTH, 1);

            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(startDate);
            startCal.set(Calendar.DAY_OF_MONTH, 1);

            Calendar cal = (Calendar) currentCal.clone();
            while (!cal.before(startCal)) {
                String monthKey = Utils.getMonthFromDate(cal.getTime());
                transactionsByMonth.put(monthKey, new ArrayList<>());
                cal.add(Calendar.MONTH, -1);
            }

            for (TransactionEntity transaction : transactions) {
                String monthKey = Utils.getMonthFromDate(transaction.getDate());
                transactionsByMonth.computeIfAbsent(monthKey, key -> new ArrayList<>()).add(transaction);
            }

            return transactionsByMonth;
        });
    }

    public LiveData<List<TransactionEntityWithCategory>> getLastTransactions() {
        return transactionDao.getLastTransactions();
    }

    public LiveData<List<TransactionEntityWithCategory>> getTransactionsWithCategory() {
        return transactionDao.getAll();
    }

    public LiveData<Long> getSpentAmount(String categoryId, long startDate, long endDate) {
        return transactionDao.getSumForCategoryAndDateRange(categoryId, startDate, endDate);
    }

    public void insertTransaction(TransactionEntity transaction, GenericCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                transactionDao.insertOrUpdate(transaction);
                callback.onSuccess(true);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting transaction: " + e.getMessage(), e);
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void delete(List<TransactionEntity> transactions) {
        executorService.execute(() -> {
            for (TransactionEntity transaction : transactions) {
                transactionDao.deleteById(transaction.getId());
            }
        });
    }

    @Override
    protected CompletableFuture<Long> syncLocalToRemoteAsync() {
        return CompletableFuture.supplyAsync(() -> {
                    List<TransactionEntity> unsyncedTransactions = transactionDao.getUnsyncedTransactions();
                    List<CompletableFuture<Void>> syncFutures = new ArrayList<>();

                    for (TransactionEntity transaction : unsyncedTransactions) {
                        transaction.setSynced(true);

                        String documentId = transaction.getFirestoreId();
                        DocumentReference docRef;

                        if (documentId == null || documentId.isEmpty()) {
                            docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                            transaction.setFirestoreId(docRef.getId());
                        } else {
                            docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
                        }

                        CompletableFuture<Void> future = runFirestoreSet(docRef, transaction)
                                .thenRunAsync(() -> transactionDao.setSynced(transaction.getId()), executorService);

                        syncFutures.add(future);
                    }

                    return syncFutures;
                }, executorService)
                .thenCompose(syncFutures -> CompletableFuture.allOf(syncFutures.toArray(new CompletableFuture[0])))
                .thenApply(v -> transactionDao.getLastSyncedTimestamp())
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing transactions to remote", e);
                    return 0L;
                });
    }

    private CompletableFuture<Void> runFirestoreSet(DocumentReference docRef, TransactionEntity transaction) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        docRef.set(transaction)
                .addOnSuccessListener(executorService, aVoid -> future.complete(null))
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error syncing transaction to remote", e)));

        return future;
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return runFirestoreQuery(lastSyncedTimestamp)
                .thenAcceptAsync(querySnapshot -> {
                    for (TransactionEntity remoteTransaction : querySnapshot.toObjects(TransactionEntity.class)) {
                        remoteTransaction.setSynced(true);

                        TransactionEntity localTransaction = transactionDao.getByFirestoreId(remoteTransaction.getFirestoreId());
                        if (localTransaction == null) {
                            transactionDao.insertOrUpdate(remoteTransaction);
                        } else {
                            TransactionEntity resolvedTransaction = resolveConflict(localTransaction, remoteTransaction);
                            transactionDao.insertOrUpdate(resolvedTransaction);
                        }
                    }
                }, executorService)
                .exceptionally(e -> {
                    Log.e(TAG, "Error syncing transactions from remote", e);
                    return null;
                });
    }

    private CompletableFuture<QuerySnapshot> runFirestoreQuery(long lastSyncedTimestamp) {
        CompletableFuture<QuerySnapshot> future = new CompletableFuture<>();

        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("lastSyncedAt", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(executorService, future::complete)
                .addOnFailureListener(e -> future.completeExceptionally(new RuntimeException("Error fetching transactions from remote", e)));

        return future;
    }

    private TransactionEntity resolveConflict(TransactionEntity local, TransactionEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
