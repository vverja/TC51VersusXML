package com.vereskul.tc51versusxml.data.network.dto


import com.google.gson.annotations.SerializedName

data class GoodsDTO (
    @SerializedName("order_ref")
    var orderRef: String,
    @SerializedName("goods_id")
    var goodsId: String,
    @SerializedName("code")
    var code: String,
    @SerializedName("name")
    var name  : String? = null,
    @SerializedName("units")
    var units : String? = null,
    @SerializedName("qty")
    var qty   : Double? = null,
    @SerializedName("price")
    var price : Double? = null,
    @SerializedName("barcode")
    var barcode: String? = null
)
