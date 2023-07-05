package com.vereskul.tc51versusxml.domain.usecases.orders_case

import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.SaveResult

class SendBarcodeTo1c(private val repository: OrdersRepository) {
    suspend operator fun invoke(barcode: String):SaveResult{
        return repository.sendBarcodeTo1c(barcode)
    }
}
