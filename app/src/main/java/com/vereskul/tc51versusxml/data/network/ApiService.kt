package com.vereskul.tc51versusxml.data.network

import com.vereskul.tc51versusxml.data.network.dto.ItemDTO
import com.vereskul.tc51versusxml.data.network.dto.NetworkContainer
import com.vereskul.tc51versusxml.data.network.dto.SaveResultDTO
import com.vereskul.tc51versusxml.data.network.dto.StockDTO
import com.vereskul.tc51versusxml.data.network.dto.SupplierDTO
import com.vereskul.tc51versusxml.data.network.dto.SupplierOrderDTO
import com.vereskul.tc51versusxml.data.network.dto.UserDTO
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
    @GET("suppliers_orders")
    suspend fun getSupplierOrders(): NetworkContainer
    @POST("users")
    suspend fun getUserInfo(): UserDTO
    @POST("suppliers_orders")
    suspend fun makeOrder(supplierOrderDTO: SupplierOrderDTO): SaveResultDTO
    @GET("supliers_list")
    suspend fun getSuppliers(): List<SupplierDTO>
    @GET("goods/shops")
    suspend fun getStocks(): List<StockDTO>
    @GET("goods_list")
    suspend fun getItems(): List<ItemDTO>

    @GET("inner_orders/by_supplier")
    suspend fun getInnerOrders(): NetworkContainer
    @POST("inner_orders")
    suspend fun saveInnerOrder(supplierOrderDTO: SupplierOrderDTO): SaveResultDTO
}