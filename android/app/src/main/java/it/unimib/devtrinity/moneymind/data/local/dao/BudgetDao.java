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

    @Query("SELECT * FROM budgets WHERE deleted = 0 AND synced = 0")
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

    @Query("SELECT * FROM budgets WHERE firestoreId = :firestoreId")
    BudgetEntity getByFirestoreId(String firestoreId);

}
