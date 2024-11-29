/*package it.unimib.devtrinity.moneymind.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import androidx.room.Update;
import it.unimib.devtrinity.moneymind.data.local.entity.UserEntity;

@Dao
public interface UserDao {
    @Insert
    void insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    UserEntity getUserById(String userId);

    @Query("DELETE FROM users WHERE userId = :userId")
    void deleteUser(String userId);
}
 */