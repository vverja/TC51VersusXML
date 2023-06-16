package com.vereskul.tc51versusxml.data.database

import androidx.room.*
import com.vereskul.tc51versusxml.data.database.entities.GoodsEntity

@Dao
interface GoodsDAO {
    @Query("SELECT * FROM supplier_order_goods WHERE order_id=:orderId")
    suspend fun getGoodsByOrderId(orderId:Int):List<GoodsEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goodsList: List<GoodsEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goods: GoodsEntity)
    @Delete
    suspend fun delete(goods: GoodsEntity)

    @Delete
    fun deleteAll(goodsList: List<GoodsEntity>)
}