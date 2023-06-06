package com.vereskul.tc51versusxml.domain.usecases.online_order_usecase

import com.vereskul.tc51versusxml.domain.OnlineOrderRepository
import com.vereskul.tc51versusxml.domain.models.ItemModel

class GetItemsUseCase(private val repository: OnlineOrderRepository) {
    suspend operator fun invoke(): List<ItemModel> {
        return repository.getItemsList()
    }
}