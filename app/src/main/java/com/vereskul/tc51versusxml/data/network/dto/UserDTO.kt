package com.vereskul.tc51versusxml.data.network.dto

import com.google.gson.annotations.SerializedName
import com.vereskul.tc51versusxml.data.database.entities.UsersEntity
import com.vereskul.tc51versusxml.domain.models.UsersModel

data class UserDTO (
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("stock_code")
    val stockCode:String? = null,
    @SerializedName("stock_name")
    val stockName:String? = null
){
    fun convertToModel() = UsersModel(
        displayName = this.userId?.trim(),
        stockCode = this.stockCode,
        stockName = this.stockName
    )

    fun convertToDataBaseModel(password: String) = UsersEntity(
        displayName = this.userId?.trim(),
        stockCode = this.stockCode,
        stockName = this.stockName,
        password = password
    )
}