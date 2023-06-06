package com.vereskul.tc51versusxml.domain.usecases.orders_case

import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class GetOrderByIdUseCase(private val repository: OrdersRepository) {
    suspend operator fun invoke(orderId:String):SupplierOrderModel{
        return repository.getOrderByOrderId(orderId)
    }
}