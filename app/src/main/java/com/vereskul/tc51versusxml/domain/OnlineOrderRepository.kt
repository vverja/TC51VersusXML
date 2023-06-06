package com.vereskul.tc51versusxml.domain

import androidx.lifecycle.LiveData
import com.vereskul.tc51versusxml.domain.models.ItemModel
import com.vereskul.tc51versusxml.domain.models.StockModel
import com.vereskul.tc51versusxml.domain.models.SupplierModel
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

interface OnlineOrderRepository {
    suspend fun getSuppliersList(): List<SupplierModel>
    suspend fun getStockList(): List<StockModel>
    suspend fun getItemsList():List<ItemModel>
    suspend fun saveOrder(supplierOrderModel: SupplierOrderModel)
    suspend fun saveBarcode()
}