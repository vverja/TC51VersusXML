package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import com.google.gson.Gson
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.data.database.entities.SupplierOrdersWithGoodsEntity
import com.vereskul.tc51versusxml.data.database.entities.UploadListEntity
import com.vereskul.tc51versusxml.data.database.entities.asDTO
import com.vereskul.tc51versusxml.data.database.entities.asDomainModel
import com.vereskul.tc51versusxml.data.database.entities.changeRefId
import com.vereskul.tc51versusxml.data.network.ApiService
import com.vereskul.tc51versusxml.data.network.dto.SaveResultDTO
import com.vereskul.tc51versusxml.data.network.dto.SupplierOrderDTO
import com.vereskul.tc51versusxml.data.network.dto.asDataBaseModel
import com.vereskul.tc51versusxml.domain.OrdersRepository
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SaveResult
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

    override suspend fun getAllOrders(inner: Boolean): List<SupplierOrderModel> {
        return if(inner){
            supplierOrdersDAO.getInnerOrders().asDomainModel()
        } else {
            supplierOrdersDAO.getAdventOrders().asDomainModel()
        }
    }

    override suspend fun getOrderByOrderId(orderId: String): SupplierOrderModel {
        return supplierOrdersDAO.getOrderById(orderId).asDomainModel().first()
    }

    override suspend fun getAdventOrdersByStatus(status: OrderStatus): List<SupplierOrderModel> {
        return supplierOrdersDAO.getOrdersByStatusAndNotInner(status.name).asDomainModel()
    }

    override suspend fun getInnerOrdersByStatus(status: OrderStatus): List<SupplierOrderModel> {
        return supplierOrdersDAO.getOrdersByStatusAndInner(status.name).asDomainModel()
    }


    override suspend fun beginWork(orderModel: SupplierOrderModel) {
        orderModel.startTime = LocalDateTime.now()
        orderModel.orderState = OrderStatus.IN_WORK
        registerForUpload(orderModel)
    }

    override suspend fun endWork(orderModel: SupplierOrderModel) {
        orderModel.endTime = LocalDateTime.now()
        orderModel.orderState = OrderStatus.IN_STOCK
        registerForUpload(orderModel)
    }

    private suspend fun registerForUpload(supplierOrdersWithGoods: SupplierOrderModel) {
        saveOrderToDb(supplierOrdersWithGoods)
        val ordersUploadList = supplierOrdersDAO.getOrdersUploadList(
            listOf(supplierOrdersWithGoods.orderId)
        )
        if (ordersUploadList.isEmpty()){
                supplierOrdersDAO.registerForUpload(
                    UploadListEntity(orderId = supplierOrdersWithGoods.orderId)
                )
            }

        dataIsChangedForWorker.value = true
    }

    private suspend fun saveOrderToDb(supplierOrdersWithGoods: SupplierOrderModel) {
        val supplierOrderEntity = supplierOrdersWithGoods.asDatabaseEntity()
        supplierOrdersDAO.insertOrder(
            supplierOrderEntity.supplierOrder
        )
        Log.d("OrdersRepositoryImpl", "${supplierOrderEntity.supplierOrder}")
        goodsDAO.insertAll(supplierOrderEntity.goods)
        Log.d("OrdersRepositoryImpl", "${supplierOrderEntity.goods}")
    }

    override suspend fun downloadOrders(): Int {
        return try {
            val ordersList = getDataFromNetwork()
            dataIsChangedForWorker.value = true
            writeToDB(ordersList.toMutableList())
        } catch (e: Exception) {
            Log.e("downloadOrders", e.message.toString())
            0
        }
    }

    private suspend fun writeToDB(ordersListFromNetwork: MutableList<SupplierOrdersWithGoodsEntity>): Int {
        val filteredNetworkList = filterList(ordersListFromNetwork)
        val orderHeader = filteredNetworkList.map { it.supplierOrder }.toList()
        Log.d("downloadOrders", orderHeader.toString())
        orderHeader.forEach { it.downloadDate = LocalDateTime.now() }

        supplierOrdersDAO.insertAllOrders(orderHeader)
        val orderTablePart = mutableListOf<GoodsEntity>()
        filteredNetworkList.forEach { orderTablePart.addAll(it.goods) }

        goodsDAO.insertAll(orderTablePart.toList())
        return orderHeader.size
    }

    private suspend fun filterList(ordersListFromNetwork: MutableList<SupplierOrdersWithGoodsEntity>): List<SupplierOrdersWithGoodsEntity> {
        var ordersAlreadyInDb = supplierOrdersDAO.getOrderByListOfId(ordersListFromNetwork.map {
            it.supplierOrder.orderId
        })
        ordersAlreadyInDb = ordersAlreadyInDb.filter {
            it.supplierOrder.orderState != OrderStatus.NEW.toString()
        }
        return ordersListFromNetwork
            .filter { inNetwork ->
                !ordersAlreadyInDb.any { inDb ->
                    inDb.supplierOrder.orderId == inNetwork.supplierOrder.orderId
                }
            }

    }

    private suspend fun getDataFromNetwork(): List<SupplierOrdersWithGoodsEntity>{
        Log.d("downloadOrders", "download begins")
        val networkContainer = apiService.getSupplierOrders()
        Log.d("downloadOrders", "$networkContainer")
        val innerOrdersNetworkContainer = apiService.getInnerOrders()
       val supplierOrdersList =
            networkContainer.asDataBaseModel().filter {
                it.supplierOrder.orderState == OrderStatus.NEW.toString()
            }.toMutableList()
        Log.d("downloadOrders", "$innerOrdersNetworkContainer")
        val innerOrders = innerOrdersNetworkContainer.asDataBaseModel().filter {
            it.supplierOrder.orderState == OrderStatus.NEW.toString()
        }.toList()
        Log.d("downloadOrders", "get orders $supplierOrdersList")
        Log.d("downloadOrders", "get inner orders $innerOrders")
        supplierOrdersList.addAll(innerOrders)
        return supplierOrdersList
    }

    override suspend fun uploadOrders(): Int =
        try {
            Log.d("uploadOrders", "Upload begins")
            val orders = supplierOrdersDAO.getOrdersUploadListByInnerJoin()

            val orderDTOList = orders.asDTO()
            orderDTOList.forEach {
                it.goods.forEach { goods ->
                    goods.name = goods.name?.replace(',', ' ')
                }
            }
            sendListToServer(orderDTOList).forEach { pair ->
                if (!pair.first.error) {
                    orders.firstOrNull { ordersEntity ->
                        ordersEntity.supplierOrder.orderId == pair.second
                    }?.apply {
                        val refAndNumber = pair.first.message.split(";")
                        setRefAndNumber(this, refAndNumber)
                        supplierOrdersDAO.deleteUploadList()
                        Log.d("uploadOrders", "upload list cleared")
                    }
                } else {
                    Log.e("uploadOrders", pair.first.message)
                }
            }
            orders.size
        } catch (e: Exception) {
            Log.e("uploadOrders", e.message.toString())
            0
        }


    private suspend fun setRefAndNumber(
        orderEntity: SupplierOrdersWithGoodsEntity,
        refAndNumber: List<String>
    ) {
        if (refAndNumber.isEmpty()) {
            throw RuntimeException("Нет данных о ссылке и номере заказа")
        }
        supplierOrdersDAO.deleteOrder(orderEntity.supplierOrder)
        Log.d("uploadOrders", " order ${orderEntity.supplierOrder} deleted")
        goodsDAO.deleteAll(orderEntity.goods)

        orderEntity.changeRefId(refAndNumber[0], refAndNumber[1])
        supplierOrdersDAO.insertOrder(orderEntity.supplierOrder)
        Log.d("uploadOrders", " order ${orderEntity.supplierOrder} inserted")
        goodsDAO.insertAll(orderEntity.goods)
    }

    private suspend fun sendListToServer(
        orderList: List<SupplierOrderDTO>
    ): List<Pair<SaveResultDTO, String>> = orderList.map {
        if (it.supplierCode.isNotEmpty()) {
            Log.d("uploadOrders", Gson().toJson(it))
            apiService.makeOrder(it) to it.orderRef
        } else {
            Log.d("uploadOrders", Gson().toJson(it))
            apiService.saveInnerOrder(it) to it.orderRef
        }
    }

    override fun getDataChangesObserver() = dataIsChangedForWorker
    override suspend fun sendBarcodeTo1c(barcode: String): SaveResult {
        TODO("Not yet implemented")
    }

    override suspend fun  closeOrder(orderModel: SupplierOrderModel): SaveResult {
        return try {
            if (orderModel.endTime==null) {
                saveOrderToDb(orderModel)
            }
            SaveResult(success = R.string.order_is_saved)
        }catch (e: Exception){
            Log.d(TAG, "Error in saving order ${e.message}")
            SaveResult(error = R.string.error_order_saving)
        }
    }


    companion object{
        private const val TAG = "OrdersRepositoryImpl"
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