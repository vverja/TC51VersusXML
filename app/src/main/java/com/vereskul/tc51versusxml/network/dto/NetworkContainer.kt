package com.vereskul.tc51versusxml.network.dto

import com.vereskul.tc51versusxml.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.database.entities.SupplierOrderEntity
import com.vereskul.tc51versusxml.database.entities.SupplierOrdersWithGoodsEntity
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

data class NetworkContainer(val ordersList: List<SupplierOrderDTO>)

fun NetworkContainer.asDataBaseModel():List<SupplierOrdersWithGoodsEntity>{
    return ordersList.map {
        SupplierOrdersWithGoodsEntity(
            supplierOrder = SupplierOrderEntity(
                orderId = it.orderRef,
                downloadDate = it.downloadDate,
                startTime = it.startTime,
                endTime = it.endTime,
                number = it.number,
                date = it.date,
                supplier = it.supplier,
                stock = it.stock,
                amount = it.amount,
                orderState = it.orderState
            ), goods = it.goods.map {goodsDTO ->
                GoodsEntity(
                    goodsId = goodsDTO.goodsId,
                    orderId = goodsDTO.orderRef,
                    name = goodsDTO.name,
                    units = goodsDTO.units,
                    qty = goodsDTO.qty,
                    price = goodsDTO.price,
                    barcode = goodsDTO.barcode
                )
            }
        )
    }
}
fun NetworkContainer.asDomainModel():List<SupplierOrderModel>{
    return ordersList.map {
        SupplierOrderModel(
            orderId = it.orderRef,
            number = it.number,
            date = it.date,
            supplier = it.supplier,
            stock = it.stock,
            amount = it.amount,
            downloadDate = it.downloadDate,
            startTime = it.startTime,
            endTime = it.endTime,
            orderState = OrderStatus.valueOf(it.orderState),
            goods = it.goods.map {goodsDto->
                GoodsModel(
                    name = goodsDto.name,
                    units = goodsDto.units,
                    qty = goodsDto.qty,
                    price = goodsDto.price,
                    barcode = goodsDto.barcode
                )
            }
        )
    }
}



