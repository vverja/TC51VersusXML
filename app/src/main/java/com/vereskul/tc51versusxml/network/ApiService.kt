package com.vereskul.tc51versusxml.network

import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.models.UsersModel
import com.vereskul.tc51versusxml.network.dto.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
    @GET("suppliers_orders")
    suspend fun getSupplierOrders():NetworkContainer
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
    suspend fun getInnerOrders():NetworkContainer
    @POST("inner_orders")
    suspend fun saveInnerOrder(supplierOrderDTO: SupplierOrderDTO): SaveResultDTO
}