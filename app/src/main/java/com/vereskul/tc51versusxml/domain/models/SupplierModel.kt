package com.vereskul.tc51versusxml.domain.models

data class SupplierModel(
    val code: String,
    val name: String
){
    override fun toString(): String {
        return this.name
    }
}
