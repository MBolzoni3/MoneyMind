package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE synced = 0")
    List<TransactionEntity> getUnsyncedTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(TransactionEntity transaction);

    @Query("UPDATE transactions SET synced = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    void setSynced(int id);

    @Query("SELECT * FROM transactions WHERE firestoreId = :firestoreId")
    TransactionEntity getByFirestoreId(String firestoreId);

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND date >= :startDate AND date <= :endDate")
    LiveData<Long> getSumForCategoryAndDateRange(String categoryId, long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE strftime('%m',date) = :month AND deleted=0")
    LiveData<List<TransactionEntity>> selectTransactions(int month);
}
