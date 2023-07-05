package com.vereskul.tc51versusxml.domain.usecases.orders_case

import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SaveResult
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class CloseOrderUseCase(private val repository: OrdersRepository) {
    suspend operator fun invoke(orderModel: SupplierOrderModel):SaveResult {
        return repository.closeOrder(orderModel)
    }
}