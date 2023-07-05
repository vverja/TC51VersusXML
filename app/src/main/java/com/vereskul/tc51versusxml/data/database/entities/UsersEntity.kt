package com.vereskul.tc51versusxml.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vereskul.tc51versusxml.domain.models.UsersModel
import java.time.LocalDateTime

@Entity(tableName = "users")
data class UsersEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="display_name")
    var displayName:String,
    @ColumnInfo("password")
    var password:String? = null,
    @ColumnInfo("stock_code")
    var stockCode: String? = null,
    @ColumnInfo("stock_name")
    var stockName: String? = null,
    @ColumnInfo("last_login")
    var lastLogin: LocalDateTime
)

fun UsersEntity.asDomainModel():UsersModel{
    return UsersModel(
        displayName = this.displayName,
        stockCode = this.stockCode,
        stockName = this.stockName
    )
}