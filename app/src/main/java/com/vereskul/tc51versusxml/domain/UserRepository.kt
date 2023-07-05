package com.vereskul.tc51versusxml.domain

import com.vereskul.tc51versusxml.domain.models.LoginResult
import com.vereskul.tc51versusxml.domain.models.UsersModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(username: String, password: String): Flow<LoginResult>
    fun getDefaultUser(): Flow<UsersModel>
}