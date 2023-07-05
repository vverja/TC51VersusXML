package com.vereskul.tc51versusxml.domain

import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SaveResult
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import kotlinx.coroutines.flow.MutableStateFlow

interface OrdersRepository {
    suspend fun getAllOrders(inner: Boolean): List<SupplierOrderModel>
    suspend fun getOrderByOrderId(orderId: String): SupplierOrderModel
    suspend fun getAdventOrdersByStatus(status: OrderStatus): List<SupplierOrderModel>
    suspend fun getInnerOrdersByStatus(status: OrderStatus): List<SupplierOrderModel>
//    suspend fun changeOrderStatus(orderId: String, orderStatus: OrderStatus):SupplierOrderModel
    suspend fun beginWork(supplierOrdersWithGoods: SupplierOrderModel)
    suspend fun endWork(supplierOrdersWithGoods: SupplierOrderModel)
    suspend fun downloadOrders(): Int
    suspend fun uploadOrders(): Int
    fun getDataChangesObserver(): MutableStateFlow<Boolean>
    suspend fun sendBarcodeTo1c(barcode: String): SaveResult
    suspend fun closeOrder(orderModel: SupplierOrderModel): SaveResult

}