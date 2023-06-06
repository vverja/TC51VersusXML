package com.vereskul.tc51versusxml.network.dto

import com.vereskul.tc51versusxml.domain.models.ItemModel

data class ItemDTO(
    val code: String,
    val name: String,
    val units: String,
    val barcode: String
)

fun List<ItemDTO>.asDomainModel():List<ItemModel>{
    return this.map {
        ItemModel(
            code = it.code,
            name = it.name,
            units = it.units,
            barcode = it.barcode
        )
    }
}