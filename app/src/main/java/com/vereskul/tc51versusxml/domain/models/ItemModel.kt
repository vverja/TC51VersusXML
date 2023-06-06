package com.vereskul.tc51versusxml.domain.models

data class ItemModel(
    val code: String,
    val name: String,
    val units: String,
    val barcode: String
){
    override fun toString(): String {
        return this.name
    }
}
