package com.vereskul.tc51versusxml.domain.models

data class StockModel(
    val code: String,
    val name: String
){
    override fun toString(): String {
        return this.name
    }
}
