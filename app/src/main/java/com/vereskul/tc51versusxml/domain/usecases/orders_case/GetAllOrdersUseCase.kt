package com.vereskul.tc51versusxml.domain.usecases.orders_case

import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class GetAllOrdersUseCase(private val ordersRepository: OrdersRepository) {
    suspend operator fun invoke(inner: Boolean): List<SupplierOrderModel> {
        return ordersRepository.getAllOrders(inner)
    }
}