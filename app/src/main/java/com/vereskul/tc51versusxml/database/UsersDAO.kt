package com.vereskul.tc51versusxml.database

import androidx.room.*
import com.vereskul.tc51versusxml.database.entities.UsersEntity

@Dao
interface UsersDAO {
    @Query("SELECT * FROM users WHERE user_id=:id")
    suspend fun getUserById(id: Int):UsersEntity

    @Delete
    suspend fun deleteUser(user: UsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UsersEntity)
}