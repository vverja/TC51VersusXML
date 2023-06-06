package com.vereskul.tc51versusxml.database.entities


import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "supplier_order_goods",
    foreignKeys = [ForeignKey(
        entity = SupplierOrderEntity::class,
        parentColumns = ["order_id"],
        childColumns = ["order_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("order_id")]
)
data class GoodsEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("goods_id")
    var goodsId: String,
    @ColumnInfo("order_id")
    var orderId: String? = null,
    @ColumnInfo("name")
    var name  : String? = null,
    @ColumnInfo("units")
    var units : String? = null,
    @ColumnInfo("qty")
    var qty   : Double? = null,
    @ColumnInfo("price")
    var price : Double? = null,
    @ColumnInfo("barcode")
    var barcode: String? = null
)
