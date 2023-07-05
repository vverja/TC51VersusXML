package com.vereskul.tc51versusxml.domain.usecases.orders_case

import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class GetInnerOrdersByStatus(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(status: OrderStatus): List<SupplierOrderModel> {
        return repository.getInnerOrdersByStatus(status)
    }
}