package com.vereskul.tc51versusxml.data.database.entities

import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

//@Entity(tableName = "supplier_orders_with_goods")
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
            supplier = it.supplierOrder.supplier,
            stock = it.supplierOrder.stock,
            amount = it.supplierOrder.amount,
            downloadDate = it.supplierOrder.downloadDate,
            startTime = it.supplierOrder.startTime,
            endTime = it.supplierOrder.endTime,
            orderState = OrderStatus.valueOf(it.supplierOrder.orderState),
            goods = it.goods.map {goodsEntity ->
                GoodsModel(
                    name = goodsEntity.name,
                    units = goodsEntity.units,
                    qty = goodsEntity.qty,
                    price = goodsEntity.price,
                    barcode = goodsEntity.barcode
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
            supplier = supplierOrder.supplier,
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
                    price = goodsEntity.price,
                    barcode = goodsEntity.barcode
                )
            }
        )
}
