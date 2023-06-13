package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.data.database.entities.asDomainModel
import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.data.network.ApiService
import com.vereskul.tc51versusxml.data.network.dto.asDataBaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrdersRepositoryImpl(
    db: AppDb,
    private val apiService: ApiService
) : OrdersRepository {

    private val supplierOrdersDAO = db.getSupplierOrdersDAO()
    private val goodsDAO = db.getGoodsDAO()

    override fun getOrdersByStockId(): LiveData<List<SupplierOrderModel>> {
        return supplierOrdersDAO.getAllSupplierOrders().map { it.asDomainModel() }
    }

    override suspend fun getOrderByOrderId(orderId: String): SupplierOrderModel {
        return supplierOrdersDAO.getOrderById(orderId).asDomainModel()
    }

    override fun getOrdersByStockIdAndStatus(status: OrderStatus): LiveData<List<SupplierOrderModel>> {
        return supplierOrdersDAO.getOrdersByStatus(status.name).map { it.asDomainModel() }
    }


    override suspend fun beginWork(supplierOrdersWithGoods: SupplierOrderModel) {
        TODO("Not yet implemented")
    }

    override suspend fun endWork(supplierOrdersWithGoods: SupplierOrderModel) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshOrdersInDb() {
        try {
            withContext(Dispatchers.IO) {
                val networkContainer = apiService.getSupplierOrders()
                val supplierOrdersList =
                    networkContainer.asDataBaseModel().filter {
                        it.supplierOrder.orderState == OrderStatus.NEW.toString()
                    }.toList()
                val innerOrdersNetworkContainer = apiService.getInnerOrders()
                val innerOrders = innerOrdersNetworkContainer.asDataBaseModel().filter {
                    it.supplierOrder.orderState == OrderStatus.NEW.toString()
                }.toList()
                val supplierOrderEntityList = supplierOrdersList.map { it.supplierOrder }.toList()
                val innerOrderEntityList = innerOrders.map {
                    it.supplierOrder
                }.toList()
                supplierOrdersDAO.insertAllOrders(supplierOrderEntityList)
                supplierOrdersDAO.insertAllOrders(innerOrderEntityList)
                val goodsList = mutableListOf<GoodsEntity>()
                supplierOrdersList.forEach{ goodsList.addAll(it.goods) }
                innerOrders.forEach{ goodsList.addAll(it.goods) }
                goodsDAO.insertAll(goodsList.toList())
            }
        }catch(e: Exception){
            Log.e("OrdersRepositoryImpl", e.message.toString())
        }
    }

    companion object{
        private val TAG = "OrdersRepositoryImpl"
        private var repository: OrdersRepositoryImpl? = null
        fun getInstance(appDb: AppDb, apiService: ApiService)= repository ?: synchronized(this){
            repository ?: OrdersRepositoryImpl(appDb, apiService).also {
                repository = it
            }
        }

    }
}