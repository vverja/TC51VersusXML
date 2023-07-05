package com.vereskul.tc51versusxml.presentation.ui.order

data class OrderFormState (
    val qtyFactError: Int? = null,
    val barcodeError: Int? = null,
    val endWorkError: Int? = null,
    val isDataValid:Boolean = false
        )