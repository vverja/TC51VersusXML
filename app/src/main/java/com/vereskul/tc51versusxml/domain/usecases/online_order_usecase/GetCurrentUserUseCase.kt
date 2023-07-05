package com.vereskul.tc51versusxml.domain.usecases.online_order_usecase

import com.vereskul.tc51versusxml.domain.OnlineOrderRepository
import com.vereskul.tc51versusxml.domain.models.UsersModel

class GetCurrentUserUseCase(private val repository: OnlineOrderRepository){
    suspend operator fun invoke():UsersModel?{
        return repository.getCurrentUser()
    }
}