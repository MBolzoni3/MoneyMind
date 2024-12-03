package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;

@Dao
public interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions WHERE deleted = 0 AND synced = 0")
    List<RecurringTransactionEntity> getUnsyncedTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(RecurringTransactionEntity transaction);

    @Query("SELECT * FROM recurring_transactions WHERE firestoreId = :firestoreId")
    RecurringTransactionEntity getByFirestoreId(String firestoreId);

}
