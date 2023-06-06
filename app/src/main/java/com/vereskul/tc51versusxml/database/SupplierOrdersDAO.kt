package com.vereskul.tc51versusxml.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vereskul.tc51versusxml.database.entities.SupplierOrderEntity
import com.vereskul.tc51versusxml.database.entities.SupplierOrdersWithGoodsEntity

@Dao
interface SupplierOrdersDAO {
    @Transaction
    @Query("SELECT * from supplier_orders")
    fun getAllSupplierOrders(): LiveData<List<SupplierOrdersWithGoodsEntity>>
    @Transaction
    @Query("SELECT * from supplier_orders WHERE order_id =:orderId")
    suspend fun getOrderById(orderId:String):SupplierOrdersWithGoodsEntity
    @Transaction
    @Query("SELECT * from supplier_orders WHERE order_state =:orderStatus")
    fun getOrdersByStatus(orderStatus: String):LiveData<List<SupplierOrdersWithGoodsEntity>>
    @Delete
    suspend fun deleteOrder(order: SupplierOrderEntity)
    @Query("DELETE FROM supplier_orders")
    suspend fun deleteAll()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: SupplierOrderEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrders(order: List<SupplierOrderEntity>)

}