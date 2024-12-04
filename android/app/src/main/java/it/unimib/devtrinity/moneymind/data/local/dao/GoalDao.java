package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;

@Dao
public interface GoalDao {

    @Query("SELECT * FROM goals WHERE deleted = 0 AND synced = 0")
    List<GoalEntity> getUnsyncedGoals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(GoalEntity goal);

    @Query("SELECT * FROM goals WHERE firestoreId = :firestoreId")
    GoalEntity getByFirestoreId(String firestoreId);

}
