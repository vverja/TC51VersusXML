package com.vereskul.tc51versusxml.presentation.ui.online_order

data class OnlineOrderFormState(
    val supplierError: Int? = null,
    val stockError: Int? = null,
    val dateError: Int? = null,
    val goodsListError: Int? = null,
    val isDataValid:Boolean = false
)