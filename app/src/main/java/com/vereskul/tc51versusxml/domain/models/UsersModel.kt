package com.vereskul.tc51versusxml.domain.models

import com.vereskul.tc51versusxml.data.database.entities.UsersEntity
import java.time.LocalDateTime

data class UsersModel(
    var displayName: String,
    var stockCode: String?=null,
    var stockName: String?=null
){
    fun asDatabaseModel(password: String): UsersEntity{
        return UsersEntity(
            displayName = this.displayName,
            password = password,
            stockCode = this.stockCode,
            stockName = this.stockName,
            lastLogin = LocalDateTime.now()
        )
    }
}