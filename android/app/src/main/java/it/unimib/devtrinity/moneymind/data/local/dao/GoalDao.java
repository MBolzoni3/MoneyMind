package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;

@Dao
public interface GoalDao {

    @Insert
    void insert(GoalEntity goal);

    @Update
    void update(GoalEntity goal);

    @Query("SELECT * FROM goals")
    LiveData<List<GoalEntity>> selectAll();

    @Query("DELETE FROM goals WHERE id = :goalId")
    void delete(int goalId);

}
