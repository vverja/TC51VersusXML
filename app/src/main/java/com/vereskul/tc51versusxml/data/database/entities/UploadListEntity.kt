package com.vereskul.tc51versusxml.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upload_list")
data class UploadListEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("upload_id")
    val uploadId: Int = 0,
    @ColumnInfo("order_id")
    val orderId: String
)