package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntityWithCategory;

@Dao
public interface GoalDao {

    @Query("SELECT * FROM goals WHERE synced = 0")
    List<GoalEntity> getUnsyncedGoals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(GoalEntity goal);

    @Query("UPDATE goals SET synced = 1, updatedAt = :updatedAt WHERE id = :id")
    void setSynced(int id, long updatedAt);

    default void setSynced(int id) {
        setSynced(id, System.currentTimeMillis());
    }

    @Query("SELECT * FROM goals WHERE firestoreId = :firestoreId")
    GoalEntity getByFirestoreId(String firestoreId);

    @Query("UPDATE goals SET deleted = 1, synced = 0, updatedAt = :updatedAt WHERE id = :id")
    void deleteById(int id, long updatedAt);

    default void deleteById(int id) {
        deleteById(id, System.currentTimeMillis());
    }

    @Query("SELECT goals.*, " +
            "categories.firestoreId AS category_firestoreId, " +
            "categories.name AS category_name, " +
            "categories.`order` AS category_order, " +
            "categories.deleted AS category_deleted, " +
            "categories.createdAt AS category_createdAt, " +
            "categories.updatedAt AS category_updatedAt " +
            "FROM goals " +
            "LEFT JOIN categories ON goals.categoryId = categories.firestoreId " +
            "WHERE goals.deleted = 0")
    LiveData<List<GoalEntityWithCategory>> getAll();

}
