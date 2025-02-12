package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE synced = 0")
    List<TransactionEntity> getUnsyncedTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(TransactionEntity transaction);

    @Query("UPDATE transactions SET synced = 1, updatedAt = :updateAt WHERE id = :id")
    void setSynced(int id, long updateAt);

    default void setSynced(int id) {
        setSynced(id, System.currentTimeMillis());
    }

    @Query("SELECT * FROM transactions WHERE firestoreId = :firestoreId")
    TransactionEntity getByFirestoreId(String firestoreId);

    @Query("UPDATE transactions SET deleted = 1, synced = 0, updatedAt = :updatedAt WHERE id = :id")
    void deleteById(int id, long updatedAt);

    default void deleteById(int id) {
        deleteById(id, System.currentTimeMillis());
    }

    @Query("SELECT SUM(amount) FROM transactions " +
            "WHERE categoryId = :categoryId " +
            "AND date >= :startDate AND date <= :endDate " +
            "AND type = 'EXPENSE' " +
            "AND deleted = 0")
    LiveData<Long> getSumForCategoryAndDateRange(String categoryId, long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE deleted = 0 ORDER BY date ASC")
    LiveData<List<TransactionEntity>> selectTransactions();

    @Query("SELECT transactions.*, " +
            "categories.firestoreId AS category_firestoreId, " +
            "categories.name AS category_name, " +
            "categories.`order` AS category_order, " +
            "categories.deleted AS category_deleted, " +
            "categories.createdAt AS category_createdAt, " +
            "categories.updatedAt AS category_updatedAt " +
            "FROM transactions " +
            "LEFT JOIN categories ON transactions.categoryId = categories.firestoreId " +
            "WHERE transactions.deleted = 0 " +
            "ORDER BY transactions.date DESC, transactions.createdAt DESC ")
    LiveData<List<TransactionEntityWithCategory>> getAll();

    @Query("SELECT * FROM transactions " +
            "WHERE date BETWEEN :startDate AND :endDate " +
            "AND deleted = 0")
    LiveData<List<TransactionEntity>> selectTransactions(long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND deleted = 0 ORDER BY date DESC")
    LiveData<List<TransactionEntity>> selectTransactionsFromDate(long startDate);

    @Query("SELECT MIN(date) FROM transactions")
    LiveData<Long> getOldestTransactionDate();

    @Query("SELECT transactions.*, " +
            "categories.firestoreId AS category_firestoreId, " +
            "categories.name AS category_name, " +
            "categories.`order` AS category_order, " +
            "categories.deleted AS category_deleted, " +
            "categories.createdAt AS category_createdAt, " +
            "categories.updatedAt AS category_updatedAt " +
            "FROM transactions " +
            "LEFT JOIN categories ON transactions.categoryId = categories.firestoreId " +
            "WHERE transactions.deleted = 0 " +
            "ORDER BY transactions.date DESC, transactions.createdAt DESC " +
            "LIMIT 3")
    LiveData<List<TransactionEntityWithCategory>> getLastTransactions();

    @Query("SELECT MAX(updatedAt) FROM transactions WHERE synced = 1")
    Long getLastSyncedTimestamp();

}
