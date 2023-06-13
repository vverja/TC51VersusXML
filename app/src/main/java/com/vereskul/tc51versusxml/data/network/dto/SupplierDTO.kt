package com.vereskul.tc51versusxml.data.network.dto

import com.vereskul.tc51versusxml.domain.models.SupplierModel

data class SupplierDTO(
    val code: String,
    val name: String
    )
fun List<SupplierDTO>.asDomainModel():List<SupplierModel>{
    return this.map {
        SupplierModel(
            code = it.code,
            name = it.name
        )
    }
}