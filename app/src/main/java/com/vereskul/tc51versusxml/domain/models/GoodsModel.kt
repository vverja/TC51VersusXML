package com.vereskul.tc51versusxml.domain.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoodsModel (
    var name  : String? = null,
    var units : String? = null,
    var qty   : Double? = null,
    var price : Double? = null,
    var barcode: String? = null
):Parcelable
