package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;

@Dao
public interface CategoryDao {

    @Insert
    void insert(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CategoryEntity> categories);

    @Update
    void update(CategoryEntity category);

    @Query("SELECT * FROM categories ORDER BY `order`")
    LiveData<List<CategoryEntity>> selectAll();

    @Query("SELECT MAX(updatedAt) FROM categories")
    Long getLastSyncedTimestamp();

}
