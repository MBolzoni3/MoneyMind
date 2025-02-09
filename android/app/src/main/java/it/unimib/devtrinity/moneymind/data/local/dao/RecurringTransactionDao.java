package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntityWithCategory;

@Dao
public interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions WHERE synced = 0")
    List<RecurringTransactionEntity> getUnsyncedTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(RecurringTransactionEntity transaction);

    @Query("UPDATE recurring_transactions SET synced = 1, updatedAt = :updatedAt WHERE id = :id")
    void setSynced(int id, long updatedAt);

    default void setSynced(int id) {
        setSynced(id, System.currentTimeMillis());
    }

    @Query("SELECT * FROM recurring_transactions WHERE firestoreId = :firestoreId")
    RecurringTransactionEntity getByFirestoreId(String firestoreId);

    @Query("UPDATE recurring_transactions SET deleted = 1, synced = 0, updatedAt = :updatedAt WHERE id = :id")
    void deleteById(int id, long updatedAt);

    default void deleteById(int id) {
        deleteById(id, System.currentTimeMillis());
    }

    @Query("SELECT recurring_transactions.*, " +
            "categories.firestoreId AS category_firestoreId, " +
            "categories.name AS category_name, " +
            "categories.`order` AS category_order, " +
            "categories.deleted AS category_deleted, " +
            "categories.createdAt AS category_createdAt, " +
            "categories.updatedAt AS category_updatedAt " +
            "FROM recurring_transactions " +
            "LEFT JOIN categories ON recurring_transactions.categoryId = categories.firestoreId " +
            "WHERE recurring_transactions.deleted = 0 " +
            "AND (recurring_transactions.recurrenceEndDate IS NULL OR recurring_transactions.recurrenceEndDate >= strftime('%s', 'now') * 1000) " +
            "ORDER BY recurring_transactions.date DESC ")
    LiveData<List<RecurringTransactionEntityWithCategory>> getAll();

}
