package com.vereskul.tc51versusxml.domain.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoodsModel (
    var code: String? = null,
    var name  : String? = null,
    var units : String? = null,
    var qty   : Double? = null,
    var qtyFact: Double = 0.0,
    var price : Double? = null,
    var barcode: String? = null,
    var tare: Boolean = false
):Parcelable
