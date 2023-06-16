package com.vereskul.tc51versusxml.data.database.entities

import androidx.room.*
import java.time.LocalDateTime


@Entity(tableName = "supplier_orders")
data class SupplierOrderEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("order_id")
    var orderId:String,
    @ColumnInfo(name = "download_date", defaultValue = "CURRENT_TIMESTAMP")
    var downloadDate: LocalDateTime? = null,
    @ColumnInfo(name = "start_time")
    var startTime: LocalDateTime? = null,
    @ColumnInfo(name = "end_time")
    var endTime: LocalDateTime? = null,
    var number : String,
    var date : LocalDateTime,
    @ColumnInfo("supplier_code")
    var supplierCode: String,
    var supplier: String,
    @ColumnInfo("stock_code")
    var stockCode : String?= null,
    var stock : String?= null,
    var amount : Double? = null,
    @ColumnInfo("order_state")
    var orderState: String = "",

    )
