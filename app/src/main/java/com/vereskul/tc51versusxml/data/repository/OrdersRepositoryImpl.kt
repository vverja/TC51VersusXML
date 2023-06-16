package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.data.database.entities.UploadListEntity
import com.vereskul.tc51versusxml.data.database.entities.asDTO
import com.vereskul.tc51versusxml.data.database.entities.asDomainModel
import com.vereskul.tc51versusxml.data.database.entities.changeRefId
import com.vereskul.tc51versusxml.data.network.ApiService
import com.vereskul.tc51versusxml.data.network.dto.asDataBaseModel
import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.models.asDatabaseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime

class OrdersRepositoryImpl(
    db: AppDb,
    var apiService: ApiService
) : OrdersRepository {

    private val dataIsChangedForWorker = MutableStateFlow(false)

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
        supplierOrdersWithGoods.startTime = LocalDateTime.now()
        supplierOrdersWithGoods.orderState = OrderStatus.IN_WORK
        registerForUpload(supplierOrdersWithGoods)
    }

    override suspend fun endWork(supplierOrdersWithGoods: SupplierOrderModel) {
        supplierOrdersWithGoods.endTime = LocalDateTime.now()
        supplierOrdersWithGoods.orderState = OrderStatus.IN_STOCK

        registerForUpload(supplierOrdersWithGoods)
    }

    private suspend fun registerForUpload(supplierOrdersWithGoods: SupplierOrderModel) {
        supplierOrdersDAO.insertOrder(
            supplierOrdersWithGoods.asDatabaseEntity().supplierOrder
        )
        supplierOrdersDAO.registerForUpload(
            UploadListEntity(orderId = supplierOrdersWithGoods.orderId)
        )
    }

    override suspend fun downloadOrders() {
        try {
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
            Log.d("Downloading...", supplierOrderEntityList.toString())
            Log.d("Downloading...", innerOrderEntityList.toString())
            supplierOrdersDAO.insertAllOrders(supplierOrderEntityList)
            supplierOrdersDAO.insertAllOrders(innerOrderEntityList)
            val goodsList = mutableListOf<GoodsEntity>()
            supplierOrdersList.forEach { goodsList.addAll(it.goods) }
            innerOrders.forEach { goodsList.addAll(it.goods) }
            goodsDAO.insertAll(goodsList.toList())
            dataIsChangedForWorker.value = true
        } catch (e: Exception) {
            Log.e("OrdersRepositoryImpl", e.message.toString())
        }
    }

    override suspend fun uploadOrders() {
        supplierOrdersDAO.getOrdersUploadListByInnerJoin().collect { orders ->
            Log.d("uploadOrders", orders.toString())
            val saveResultAndOrderList = orders.asDTO().map {
                apiService.makeOrder(it) to it.orderRef
            }
            saveResultAndOrderList.forEach {
                if (!it.first.error) {
                    orders.firstOrNull { ordersEntity ->
                        ordersEntity.supplierOrder.orderId == it.second
                    }?.apply {
                        supplierOrdersDAO.deleteOrder(this.supplierOrder)
                        goodsDAO.deleteAll(this.goods)
                        this.changeRefId(it.first.message)
                        supplierOrdersDAO.insertOrder(this.supplierOrder)
                        goodsDAO.insertAll(this.goods)
                        supplierOrdersDAO.deleteUploadList()
                    }
                }else{
                    Log.e("uploadOrders", it.first.message)
                }
            }
            dataIsChangedForWorker.value = true
        }
    }

    override fun getDataChangesObserver() = dataIsChangedForWorker


    companion object{
        private val TAG = "OrdersRepositoryImpl"
        private var repository: OrdersRepositoryImpl? = null
        fun getInstance(appDb: AppDb, apiService: ApiService)= repository?.also {
            it.apiService = apiService
        } ?: synchronized(this){
            repository ?: OrdersRepositoryImpl(appDb, apiService).also {
                repository = it
            }
        }

    }
}