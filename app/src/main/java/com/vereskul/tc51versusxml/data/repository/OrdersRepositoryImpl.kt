package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import retrofit2.HttpException
import java.time.LocalDateTime
import kotlin.math.log

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
        supplierOrdersDAO.getOrdersUploadList(
            listOf(supplierOrdersWithGoods.orderId)
        ).collect{
            if (it.isEmpty()){
                supplierOrdersDAO.registerForUpload(
                    UploadListEntity(orderId = supplierOrdersWithGoods.orderId)
                )
            }
        }
        dataIsChangedForWorker.value = true
    }

    override suspend fun downloadOrders() {
        try {
            Log.d("downloadOrders", "download begins")
            val networkContainer = apiService.getSupplierOrders()
            val supplierOrdersList =
                networkContainer.asDataBaseModel().filter {
                    it.supplierOrder.orderState == OrderStatus.NEW.toString()
                }.toList()
            Log.d("downloadOrders", "$networkContainer")
            val innerOrdersNetworkContainer = apiService.getInnerOrders()
            Log.d("downloadOrders", "$innerOrdersNetworkContainer")
            val innerOrders = innerOrdersNetworkContainer.asDataBaseModel().filter {
                it.supplierOrder.orderState == OrderStatus.NEW.toString()
            }.toList()
            Log.d("downloadOrders", "get orders $supplierOrdersList")
            Log.d("downloadOrders", "get inner orders $innerOrders")
            val supplierOrderEntityList = supplierOrdersList.map { it.supplierOrder }.toList()
            val innerOrderEntityList = innerOrders.map {
                it.supplierOrder
            }.toList()
            Log.d("downloadOrders", supplierOrderEntityList.toString())
            Log.d("downloadOrders", innerOrderEntityList.toString())
            supplierOrderEntityList.forEach { it.downloadDate = LocalDateTime.now() }
            innerOrderEntityList.forEach { it.downloadDate = LocalDateTime.now() }
            supplierOrdersDAO.insertAllOrders(supplierOrderEntityList)
            supplierOrdersDAO.insertAllOrders(innerOrderEntityList)
            val goodsList = mutableListOf<GoodsEntity>()
            supplierOrdersList.forEach { goodsList.addAll(it.goods) }
            innerOrders.forEach { goodsList.addAll(it.goods) }
            goodsDAO.insertAll(goodsList.toList())
            dataIsChangedForWorker.value = true
        } catch (e: Exception) {
            Log.e("downloadOrders", e.message.toString())
        }
    }

    override suspend fun uploadOrders() {
        supplierOrdersDAO.getOrdersUploadListByInnerJoin().collect { orders ->
            Log.d("uploadOrders", "Upload begins")
            try {
                val saveResultAndOrderList = orders.asDTO()
                saveResultAndOrderList.forEach {
                        it.goods.forEach{goods ->
                            goods.name = goods.name?.replace(',', ' ')
                        }
                    }
                saveResultAndOrderList.map {
                    Log.d("uploadOrders", "$it")
                    apiService.makeOrder(it) to it.orderRef
                }.forEach {
                    if (!it.first.error) {
                        orders.firstOrNull { ordersEntity ->
                            ordersEntity.supplierOrder.orderId == it.second
                        }?.apply {
                            val refAndNumber = it.first.message.split(";")
                            Log.d("uploadOrders", it.first.toString())
                            if (refAndNumber.isEmpty()){
                                throw RuntimeException("Нет данных о ссылке и номере заказа")
                            }
                            supplierOrdersDAO.deleteOrder(this.supplierOrder)
                            Log.d("uploadOrders", " order ${this.supplierOrder} deleted")
                            goodsDAO.deleteAll(this.goods)

                            this.changeRefId(refAndNumber[0], refAndNumber[1])
                            supplierOrdersDAO.insertOrder(this.supplierOrder)
                            Log.d("uploadOrders", " order ${this.supplierOrder} inserted")
                            goodsDAO.insertAll(this.goods)
                            supplierOrdersDAO.deleteUploadList()
                            Log.d("uploadOrders", "upload list cleared")
                        }
                    } else {
                        Log.e("uploadOrders", it.first.message)
                    }
                }
                dataIsChangedForWorker.value = true
            } catch (e: Exception) {
                Log.e("uploadOrders", e.message.toString())
            }
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