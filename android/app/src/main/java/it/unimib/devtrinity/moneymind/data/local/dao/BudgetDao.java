package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;

@Dao
public interface BudgetDao {

    @Insert
    void insert(BudgetEntity budget);

    @Update
    void update(BudgetEntity budget);

    @Query("SELECT * FROM budgets")
    List<BudgetEntity> selectAll();

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    void delete(int budgetId);

}
