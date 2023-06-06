package com.vereskul.tc51versusxml.network.dto

import com.vereskul.tc51versusxml.domain.models.StockModel

data class StockDTO (
    val code: String,
    val name: String
)

fun List<StockDTO>.asDomainModel():List<StockModel>{
    return this.map {
        StockModel(
            code = it.code,
            name = it.name
        )
    }
}