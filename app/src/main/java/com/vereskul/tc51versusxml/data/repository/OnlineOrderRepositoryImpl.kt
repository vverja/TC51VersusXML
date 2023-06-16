package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.database.entities.UploadListEntity
import com.vereskul.tc51versusxml.data.database.entities.asDomainModel
import com.vereskul.tc51versusxml.data.network.ApiService
import com.vereskul.tc51versusxml.data.network.dto.asDomainModel
import com.vereskul.tc51versusxml.domain.OnlineOrderRepository
import com.vereskul.tc51versusxml.domain.models.ItemModel
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SaveResult
import com.vereskul.tc51versusxml.domain.models.StockModel
import com.vereskul.tc51versusxml.domain.models.SupplierModel
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.models.UsersModel
import com.vereskul.tc51versusxml.domain.models.asDatabaseEntity
import java.time.LocalDateTime
import java.util.UUID

class OnlineOrderRepositoryImpl(
    db: AppDb,
    private val apiService: ApiService
) : OnlineOrderRepository {

    private val supplierOrdersDAO = db.getSupplierOrdersDAO()
    private val goodsDAO = db.getGoodsDAO()
    private val usersDAO = db.getUsersDAO()


    override suspend fun getSuppliersList(): List<SupplierModel> {
        return apiService.getSuppliers().asDomainModel()
    }

    override suspend fun getStockList(): List<StockModel> {
        return apiService.getStocks().asDomainModel()
    }

    override suspend fun getItemsList(): List<ItemModel> {
        return apiService.getItems().asDomainModel()
    }

    override suspend fun saveOrder(supplierOrderModel: SupplierOrderModel): SaveResult {
        try {
            Log.d("SAVE_ORDER", "Save order begins")
            val tempRefId = UUID.randomUUID()
            supplierOrderModel.orderId = "$tempRefId"
            supplierOrderModel.orderState = OrderStatus.IN_STOCK
            val now = LocalDateTime.now()
            supplierOrderModel.downloadDate = now
            supplierOrderModel.startTime = now
            val supplierOrdersWithGoodsEntity = supplierOrderModel.asDatabaseEntity()
            supplierOrdersDAO.insertOrder(supplierOrdersWithGoodsEntity.supplierOrder)
            goodsDAO.insertAll(supplierOrdersWithGoodsEntity.goods)
            val uploadEntity = UploadListEntity(
                orderId = supplierOrdersWithGoodsEntity.supplierOrder.orderId
            )
            Log.d("SAVE_ORDER", "$uploadEntity")
            supplierOrdersDAO.registerForUpload(uploadEntity)
            return SaveResult(success = R.string.susccess_saving_order)
        }catch (e: Exception){
            Log.e("SAVE_ORDER", e.message.toString())
            return SaveResult(error = R.string.save_order_error)
        }
    }

    override suspend fun saveBarcode() {
        //TODO make online saving of barcode
    }

    override suspend fun getCurrentUser(): UsersModel {
        return usersDAO.getUser().asDomainModel()
    }

    companion object{
        private val TAG = "OnlineOrdersRepositoryImpl"
        private var repository: OnlineOrderRepositoryImpl? = null
        fun getInstance(appDb: AppDb, apiService: ApiService)= repository ?: synchronized(this){
            repository ?: OnlineOrderRepositoryImpl(appDb, apiService).also {
                repository = it
            }
        }

    }

}