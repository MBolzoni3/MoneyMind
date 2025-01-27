package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;

@Dao
public interface GoalDao {

    @Query("SELECT * FROM goals WHERE synced = 0")
    List<GoalEntity> getUnsyncedGoals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(GoalEntity goal);

    @Query("UPDATE goals SET synced = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    void setSynced(int id);

    @Query("SELECT * FROM goals WHERE firestoreId = :firestoreId")
    GoalEntity getByFirestoreId(String firestoreId);

}
