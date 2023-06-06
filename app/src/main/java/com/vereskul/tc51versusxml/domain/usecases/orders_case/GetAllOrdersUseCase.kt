package com.vereskul.tc51versusxml.domain.usecases.orders_case

import androidx.lifecycle.LiveData
import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class GetAllOrdersUseCase(private val ordersRepository: OrdersRepository) {
    operator fun invoke(): LiveData<List<SupplierOrderModel>> {
        return ordersRepository.getOrdersByStockId()
    }
}