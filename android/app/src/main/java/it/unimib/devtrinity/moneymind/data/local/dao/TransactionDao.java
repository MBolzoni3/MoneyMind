package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions")
    LiveData<List<TransactionEntity>> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionEntity transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TransactionEntity> transactions);

    @Update
    void update(TransactionEntity transaction);

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    void delete(int transactionId);

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    List<TransactionEntity> getUnsyncedTransactions();

    @Query("UPDATE transactions SET isSynced = 1 WHERE id = :transactionId")
    void markAsSynced(int transactionId);

    @Query("SELECT * FROM transactions WHERE amount > 0")
    LiveData<List<TransactionEntity>> selectPositiveTransactions();
}
