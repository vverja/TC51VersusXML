package com.vereskul.tc51versusxml.domain.usecases.online_order_usecase

import com.vereskul.tc51versusxml.domain.OnlineOrderRepository
import com.vereskul.tc51versusxml.domain.models.SaveResult
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class SaveOrderUseCase(private val repository: OnlineOrderRepository) {
    suspend operator fun invoke(supplierOrderModel: SupplierOrderModel):SaveResult {
        return repository.saveOrder(supplierOrderModel)
    }
}