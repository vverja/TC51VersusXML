package com.vereskul.tc51versusxml.domain.usecases.users_case

import com.vereskul.tc51versusxml.domain.UserRepository
import com.vereskul.tc51versusxml.domain.models.LoginResult
import kotlinx.coroutines.flow.Flow


class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String, password: String): Flow<LoginResult> {
        return repository.login(username,password)
    }
}