package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND synced = 0")
    List<TransactionEntity> getUnsyncedTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(TransactionEntity transaction);

    @Query("SELECT * FROM transactions WHERE firestoreId = :firestoreId")
    TransactionEntity getByFirestoreId(String firestoreId);

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND date >= :startDate AND date <= :endDate")
    LiveData<Long> getSumForCategoryAndDateRange(int categoryId, long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE amount > 0")
    LiveData<List<TransactionEntity>> selectPositiveTransactions();
}
