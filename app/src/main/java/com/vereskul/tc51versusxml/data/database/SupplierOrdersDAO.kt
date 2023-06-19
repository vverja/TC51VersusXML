package com.vereskul.tc51versusxml.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vereskul.tc51versusxml.data.database.entities.SupplierOrderEntity
import com.vereskul.tc51versusxml.data.database.entities.SupplierOrdersWithGoodsEntity
import com.vereskul.tc51versusxml.data.database.entities.UploadListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierOrdersDAO {
    @Transaction
    @Query("SELECT * from supplier_orders order by date desc")
    fun getAllSupplierOrders(): LiveData<List<SupplierOrdersWithGoodsEntity>>
    @Transaction
    @Query("SELECT * from supplier_orders WHERE order_id =:orderId")
    suspend fun getOrderById(orderId:String): SupplierOrdersWithGoodsEntity
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

    // upload list part
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerForUpload(uploadListEntity: UploadListEntity)

    @Transaction
    @Query("Select * from upload_list where order_id in (:orderIdList)")
    fun getOrdersUploadList(orderIdList: List<String>): Flow<List<UploadListEntity>>

    @Transaction
    @Query(
        "Select supplier_orders.* from supplier_orders " +
                "join upload_list " +
                    " on supplier_orders.order_id = upload_list.order_id"
    )
    fun getOrdersUploadListByInnerJoin(): Flow<List<SupplierOrdersWithGoodsEntity>>

    @Transaction
    @Query("Delete from upload_list")
    fun deleteUploadList()
}