package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;

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

    public TransactionRepository(Context context) {
        super(context, Constants.TRANSACTIONS_LAST_SYNC_KEY, TAG);
        this.transactionDao = DatabaseClient.getInstance(context).transactionDao();
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
