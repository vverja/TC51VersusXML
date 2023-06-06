package com.vereskul.tc51versusxml.network.dto

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


data class SupplierOrderDTO (
    @SerializedName("order_ref")
    var orderRef: String,
    var number : String,
    var date : LocalDateTime,
    var supplier: String,
    var stock : String?= null,
    var amount : Double? = null,
    @SerializedName("download_date")
    var downloadDate: LocalDateTime? = null,
    @SerializedName("start_time")
    var startTime: LocalDateTime? = null,
    @SerializedName("end_time")
    var endTime: LocalDateTime? = null,
    @SerializedName("order_state")
    var orderState: String = "",
    @SerializedName("goods")
    var goods : List<GoodsDTO> = mutableListOf()
)
