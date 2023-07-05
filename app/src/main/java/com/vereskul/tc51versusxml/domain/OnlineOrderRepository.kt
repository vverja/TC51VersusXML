package com.vereskul.tc51versusxml.domain

import com.vereskul.tc51versusxml.domain.models.ItemModel
import com.vereskul.tc51versusxml.domain.models.SaveResult
import com.vereskul.tc51versusxml.domain.models.StockModel
import com.vereskul.tc51versusxml.domain.models.SupplierModel
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.models.UsersModel

interface OnlineOrderRepository {
    suspend fun getSuppliersList(): List<SupplierModel>
    suspend fun getStockList(): List<StockModel>
    suspend fun getItemsList():List<ItemModel>
    suspend fun saveOrder(supplierOrderModel: SupplierOrderModel): SaveResult
    suspend fun saveBarcode()
    suspend fun getCurrentUser(): UsersModel?
}