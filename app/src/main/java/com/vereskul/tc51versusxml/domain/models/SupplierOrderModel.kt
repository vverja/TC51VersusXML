package com.vereskul.tc51versusxml.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.vereskul.tc51versusxml.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.database.entities.SupplierOrderEntity
import com.vereskul.tc51versusxml.database.entities.SupplierOrdersWithGoodsEntity
import com.vereskul.tc51versusxml.network.dto.GoodsDTO
import com.vereskul.tc51versusxml.network.dto.SupplierOrderDTO
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class SupplierOrderModel (
    var orderId : String,
    var number : String? = null,
    var date : LocalDateTime? = null,
    var supplier: String? = null,
    var stock : String?= null,
    var amount : Double? = null,
    var downloadDate: LocalDateTime? = null,
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var orderState: OrderStatus? = null,
    var goods : List<GoodsModel> = mutableListOf()
):Parcelable

fun SupplierOrderModel.asDTO():SupplierOrderDTO{
    return SupplierOrderDTO(
        orderRef = "",
        number = "",
        date = this.date?: LocalDateTime.now(),
        supplier = this.supplier?:"",
        stock = this.stock,
        amount = this.amount,
        downloadDate = this.downloadDate,
        startTime = this.startTime,
        endTime = this.endTime,
        orderState = this.orderState?.name?:OrderStatus.IN_STOCK.name,
        goods = this.goods.map {
            GoodsDTO(
                name = it.name,
                units = it.units,
                qty = it.qty,
                price = it.price,
                barcode = it.barcode,
                orderRef = "",
                goodsId = ""
            )
        }
    )
}

