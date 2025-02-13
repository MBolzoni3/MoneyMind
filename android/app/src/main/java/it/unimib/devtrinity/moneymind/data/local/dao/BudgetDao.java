package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;

@Dao
public interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE synced = 0")
    List<BudgetEntity> getUnsyncedBudgets();

    @Query("SELECT budgets.*, " +
            "categories.firestoreId AS category_firestoreId, " +
            "categories.name AS category_name, " +
            "categories.`order` AS category_order, " +
            "categories.deleted AS category_deleted, " +
            "categories.createdAt AS category_createdAt, " +
            "categories.updatedAt AS category_updatedAt " +
            "FROM budgets " +
            "LEFT JOIN categories ON budgets.categoryId = categories.firestoreId " +
            "WHERE budgets.deleted = 0")
    LiveData<List<BudgetEntityWithCategory>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(BudgetEntity goal);

    @Query("UPDATE budgets SET synced = 1, updatedAt = :updatedAt WHERE id = :id")
    void setSynced(int id, long updatedAt);

    default void setSynced(int id) {
        setSynced(id, System.currentTimeMillis());
    }

    @Query("SELECT * FROM budgets WHERE firestoreId = :firestoreId")
    BudgetEntity getByFirestoreId(String firestoreId);

    @Query("UPDATE budgets SET deleted = 1, synced = 0, updatedAt = :updatedAt WHERE id = :id")
    void deleteById(int id, long updatedAt);

    default void deleteById(int id) {
        deleteById(id, System.currentTimeMillis());
    }

    @Query("SELECT MAX(updatedAt) FROM budgets WHERE synced = 1")
    Long getLastSyncedTimestamp();

}
