package com.vereskul.tc51versusxml.domain.usecases.orders_case

import androidx.lifecycle.LiveData
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class GetNewOrdersUseCase(private val repository: OrdersRepository) {
    operator fun invoke(): LiveData<List<SupplierOrderModel>> {
        return repository.getOrdersByStockIdAndStatus(OrderStatus.NEW)
    }
}