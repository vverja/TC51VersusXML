package com.vereskul.tc51versusxml.domain

import androidx.lifecycle.LiveData
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

interface OrdersRepository {
    fun getOrdersByStockId(): LiveData<List<SupplierOrderModel>>
    suspend fun getOrderByOrderId(orderId: String):SupplierOrderModel
    fun getOrdersByStockIdAndStatus(status: OrderStatus):LiveData<List<SupplierOrderModel>>
//    suspend fun changeOrderStatus(orderId: String, orderStatus: OrderStatus):SupplierOrderModel
    suspend fun beginWork(supplierOrdersWithGoods: SupplierOrderModel)
    suspend fun endWork(supplierOrdersWithGoods: SupplierOrderModel)
    suspend fun refreshOrdersInDb()
}