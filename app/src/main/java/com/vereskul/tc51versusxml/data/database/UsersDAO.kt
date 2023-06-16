package com.vereskul.tc51versusxml.data.database

import androidx.room.*
import com.vereskul.tc51versusxml.data.database.entities.UsersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDAO {
    @Query("Select * FROM users LIMIT 1")
    suspend fun getUser(): UsersEntity

    @Delete
    suspend fun deleteUser(user: UsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UsersEntity)
    @Query("SELECT * FROM users WHERE display_name=:username and password =:password")
    fun getUserByNameAndPassword(username: String, password: String): Flow<List<UsersEntity>>

}