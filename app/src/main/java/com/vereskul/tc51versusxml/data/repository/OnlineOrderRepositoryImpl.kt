package com.vereskul.tc51versusxml.data.repository

import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.domain.OnlineOrderRepository
import com.vereskul.tc51versusxml.domain.models.*
import com.vereskul.tc51versusxml.data.network.ApiService
import com.vereskul.tc51versusxml.data.network.dto.asDomainModel

class OnlineOrderRepositoryImpl(
    db: AppDb,
    private val apiService: ApiService
) : OnlineOrderRepository {

    private val supplierOrdersDAO = db.getSupplierOrdersDAO()
    private val goodsDAO = db.getGoodsDAO()


    override suspend fun getSuppliersList(): List<SupplierModel> {
        return apiService.getSuppliers().asDomainModel()
    }

    override suspend fun getStockList(): List<StockModel> {
        return apiService.getStocks().asDomainModel()
    }

    override suspend fun getItemsList(): List<ItemModel> {
        return apiService.getItems().asDomainModel()
    }

    override suspend fun saveOrder(supplierOrderModel: SupplierOrderModel) {
        apiService.makeOrder(supplierOrderModel.asDTO())
    }

    override suspend fun saveBarcode() {
        //TODO make online saving of barcode
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