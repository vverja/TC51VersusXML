package com.vereskul.tc51versusxml.domain.models

import android.os.Parcelable
import com.vereskul.tc51versusxml.data.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.data.database.entities.SupplierOrderEntity
import com.vereskul.tc51versusxml.data.database.entities.SupplierOrdersWithGoodsEntity
import com.vereskul.tc51versusxml.data.network.dto.GoodsDTO
import com.vereskul.tc51versusxml.data.network.dto.SupplierOrderDTO
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

fun SupplierOrderModel.asDTO(): SupplierOrderDTO {
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
                code = it.code?:"",
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

fun SupplierOrderModel.asDatabaseEntity(): SupplierOrdersWithGoodsEntity {
    var rowId = 1
    return SupplierOrdersWithGoodsEntity(
        supplierOrder = SupplierOrderEntity(
            orderId = this.orderId,
            downloadDate = this.downloadDate,
            startTime = this.startTime,
            endTime = this.endTime,
            number = this.number?:"",
            date = this.date?: LocalDateTime.now(),
            supplier = this.supplier?:"",
            stock = this.stock,
            amount = this.amount,
            orderState = this.orderState.toString()
        ), goods = this.goods.map {
            GoodsEntity(
                goodsId = "${this.orderId}-${rowId++}",
                orderId = this.orderId,
                code = it.code,
                name = it.name,
                units = it.units,
                qty = it.qty,
                price = it.price,
                barcode = it.barcode
            )
        }
    )
}



