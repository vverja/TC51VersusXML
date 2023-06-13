package com.vereskul.tc51versusxml.domain

import com.vereskul.tc51versusxml.domain.models.LoginResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(username: String, password: String): Flow<LoginResult>
}