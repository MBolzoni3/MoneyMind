package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;

@Dao
public interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE deleted = 0 AND synced = 0")
    List<BudgetEntity> getUnsyncedBudgets();

    @Query("SELECT * FROM budgets WHERE deleted = 0")
    LiveData<List<BudgetEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(BudgetEntity goal);

    @Query("SELECT * FROM budgets WHERE firestoreId = :firestoreId")
    BudgetEntity getByFirestoreId(String firestoreId);

}
