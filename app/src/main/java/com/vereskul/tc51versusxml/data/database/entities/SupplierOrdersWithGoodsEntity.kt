package com.vereskul.tc51versusxml.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.vereskul.tc51versusxml.data.network.dto.GoodsDTO
import com.vereskul.tc51versusxml.data.network.dto.SupplierOrderDTO
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel


data class SupplierOrdersWithGoodsEntity(
    @Embedded
    var supplierOrder: SupplierOrderEntity,
    @Relation(
        parentColumn = "order_id",
        entityColumn = "order_id"
    )
    var goods: List<GoodsEntity>
)

fun List<SupplierOrdersWithGoodsEntity>.asDomainModel():List<SupplierOrderModel>{
    return map {
        SupplierOrderModel(
            orderId = it.supplierOrder.orderId,
            number = it.supplierOrder.number,
            date = it.supplierOrder.date,
            supplierCode = it.supplierOrder.supplierCode,
            supplier = it.supplierOrder.supplier,
            stockCode = it.supplierOrder.stockCode,
            stock = it.supplierOrder.stock,
            amount = it.supplierOrder.amount,
            downloadDate = it.supplierOrder.downloadDate,
            startTime = it.supplierOrder.startTime,
            endTime = it.supplierOrder.endTime,
            orderState = OrderStatus.valueOf(it.supplierOrder.orderState),
            goods = it.goods.map {goodsEntity ->
                GoodsModel(
                    code = goodsEntity.code,
                    name = goodsEntity.name,
                    units = goodsEntity.units,
                    qty = goodsEntity.qty,
                    qtyFact = goodsEntity.qtyFact,
                    price = goodsEntity.price,
                    barcode = goodsEntity.barcode
                )
            }
        )
    }
}
fun List<SupplierOrdersWithGoodsEntity>.asDTO():List<SupplierOrderDTO>{
    return map {
        SupplierOrderDTO(
            orderRef = it.supplierOrder.orderId,
            number = it.supplierOrder.number,
            date = it.supplierOrder.date,
            supplierCode = it.supplierOrder.supplierCode,
            supplier = it.supplierOrder.supplier,
            stockCode = it.supplierOrder.stockCode,
            stock = it.supplierOrder.stock,
            amount = it.supplierOrder.amount,
            downloadDate = it.supplierOrder.downloadDate,
            startTime = it.supplierOrder.startTime,
            endTime = it.supplierOrder.endTime,
            orderState = it.supplierOrder.orderState,
            goods = it.goods.map {goods->
                GoodsDTO(
                    orderRef = goods.orderId?:"",
                    goodsId = goods.goodsId,
                    code = goods.code?:"",
                    name = goods.name,
                    units = goods.units,
                    qty = goods.qty,
                    qtyFact = goods.qtyFact,
                    price = goods.price,
                    barcode = goods.barcode,
                    tare = goods.tare
                )
            }
        )
    }
}
fun SupplierOrdersWithGoodsEntity.asDomainModel():SupplierOrderModel{
    return SupplierOrderModel(
            orderId = supplierOrder.orderId,
            number =supplierOrder.number,
            date = supplierOrder.date,
            supplierCode = supplierOrder.supplierCode,
            supplier = supplierOrder.supplier,
            stockCode = supplierOrder.stockCode,
            stock = supplierOrder.stock,
            amount = supplierOrder.amount,
            downloadDate = supplierOrder.downloadDate,
            startTime = supplierOrder.startTime,
            endTime = supplierOrder.endTime,
            orderState = OrderStatus.valueOf(supplierOrder.orderState),
            goods = goods.map {goodsEntity ->
                GoodsModel(
                    name = goodsEntity.name,
                    units = goodsEntity.units,
                    qty = goodsEntity.qty,
                    qtyFact = goodsEntity.qtyFact,
                    price = goodsEntity.price,
                    barcode = goodsEntity.barcode,
                    tare = goodsEntity.tare
                )
            }
        )
}
fun SupplierOrdersWithGoodsEntity.changeRefId(newRefId: String, number: String){
    var counter = 1
    this.supplierOrder.orderId = newRefId
    this.supplierOrder.number = number
    this.goods.forEach {
        it.orderId = newRefId
        it.goodsId = "$newRefId-${counter++}"
    }
}
