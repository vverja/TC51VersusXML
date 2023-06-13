package com.vereskul.tc51versusxml.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.vereskul.tc51versusxml.domain.models.UsersModel

@Entity(tableName = "users")
data class UsersEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("user_id")
    var userId:Int = 0,
    @ColumnInfo(name="display_name")
    var displayName:String? = null,
    @ColumnInfo("password")
    var password:String? = null,
    @ColumnInfo("stock_code")
    var stockCode: String? = null,
    @ColumnInfo("stock_name")
    var stockName: String? = null
)

fun UsersEntity.asDomainModel():UsersModel{
    return UsersModel(
        displayName = this.displayName,
        stockCode = this.stockCode,
        stockName = this.stockName
    )
}