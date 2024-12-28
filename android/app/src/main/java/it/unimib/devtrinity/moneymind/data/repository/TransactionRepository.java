package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.TransactionDao;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class TransactionRepository extends GenericRepository {
    private static final String COLLECTION_NAME = "transactions";

    private final TransactionDao transactionDao;

    public TransactionRepository(Context context) {
        this.transactionDao = DatabaseClient.getInstance(context).transactionDao();
    }

    public LiveData<List<TransactionEntity>> getAllTransactions() {
        return transactionDao.selectAll();
    }

    public void getPositiveTransactions(GenericCallback<List<TransactionEntity>> callback) {
        callback.onSuccess(transactionDao.selectPositiveTransactions());
    }

    public void insertTransaction(TransactionEntity transaction) {
        executorService.execute(() -> {
            transactionDao.insert(transaction);
        });
    }

    public void uploadUnsyncedTransactions() {
        List<TransactionEntity> unsyncedTransactions = transactionDao.getUnsyncedTransactions();
        for (TransactionEntity transaction : unsyncedTransactions) {
            Map<String, Object> data = new HashMap<>();
            data.put("amount", transaction.getAmount());
            //data.put("category", transaction.getCategory());
            data.put("date", transaction.getDate());
            data.put("lastUpdated", transaction.getLastUpdated());

            FirestoreHelper.getInstance().addDocument(COLLECTION_NAME, data, new GenericCallback<String>() {
                @Override
                public void onSuccess(String documentId) {
                    transaction.setFirestoreId(documentId);
                    transaction.setSynced(true);
                    transactionDao.update(transaction);
                    Log.d("TransactionRepository", "Transaction synced: " + documentId);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("TransactionRepository", "Error syncing transaction: " + transaction.getId() + "\n" + errorMessage);
                }
            });
        }
    }

    // Download delle transazioni modificate
    public void downloadNewTransactions(long lastSyncedTimestamp) {
        Query query = FirestoreHelper.getInstance().getCollection(COLLECTION_NAME)
                .whereGreaterThan("lastUpdated", lastSyncedTimestamp);

        FirestoreHelper.getInstance().getDocuments(COLLECTION_NAME, query, new GenericCallback<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<TransactionEntity> transactions = new ArrayList<>();
                querySnapshot.forEach(document -> {
                    TransactionEntity entity = new TransactionEntity();
                    entity.setFirestoreId(document.getId());
                    entity.setAmount(document.getDouble("amount"));
                    //entity.setCategory(document.getString("category"));
                    entity.setDate(document.getString("date"));
                    entity.setLastUpdated(document.getLong("lastUpdated"));
                    entity.setSynced(true);
                    transactions.add(entity);
                });
                transactionDao.insert(transactions);
                Log.d("TransactionRepository", "New transactions downloaded");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("TransactionRepository", "Error downloading transactions\n" + errorMessage);
            }
        });
    }

}
