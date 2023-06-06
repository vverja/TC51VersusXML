package com.vereskul.tc51versusxml.domain.usecases.online_order_usecase

import androidx.lifecycle.LiveData
import com.vereskul.tc51versusxml.domain.OnlineOrderRepository
import com.vereskul.tc51versusxml.domain.models.SupplierModel

class GetSuppliersUseCase(private val repository: OnlineOrderRepository) {
    suspend operator fun invoke(): List<SupplierModel> {
        return repository.getSuppliersList()
    }
}