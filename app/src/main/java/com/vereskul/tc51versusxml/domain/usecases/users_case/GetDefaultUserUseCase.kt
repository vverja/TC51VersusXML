package com.vereskul.tc51versusxml.domain.usecases.users_case

import com.vereskul.tc51versusxml.domain.UserRepository
import com.vereskul.tc51versusxml.domain.models.UsersModel
import kotlinx.coroutines.flow.Flow

class GetDefaultUserUseCase(private val repository: UserRepository){
    operator fun invoke(): Flow<UsersModel>{
        return repository.getDefaultUser()
    }
}