package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;

@Dao
public interface CategoryDao {

    @Insert
    void insert(CategoryEntity category);

    @Update
    void update(CategoryEntity category);

    @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntity>> selectAll();

    @Query("DELETE FROM categories WHERE id = :categoryId")
    void deleteCategory(int categoryId);

}
