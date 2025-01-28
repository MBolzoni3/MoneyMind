package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;

@Dao
public interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions WHERE synced = 0")
    List<RecurringTransactionEntity> getUnsyncedTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(RecurringTransactionEntity transaction);

    @Query("UPDATE recurring_transactions SET synced = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    void setSynced(int id);

    @Query("SELECT * FROM recurring_transactions WHERE firestoreId = :firestoreId")
    RecurringTransactionEntity getByFirestoreId(String firestoreId);

}
