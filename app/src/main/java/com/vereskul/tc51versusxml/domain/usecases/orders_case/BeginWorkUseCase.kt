package com.vereskul.tc51versusxml.domain.usecases.orders_case

import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class BeginWorkUseCase(private val repository: OrdersRepository) {
    suspend operator fun invoke(supplierOrderModel: SupplierOrderModel){
        repository.beginWork(supplierOrderModel)
    }
}