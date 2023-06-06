package com.vereskul.tc51versusxml.domain.models

import com.google.gson.annotations.SerializedName

data class UsersModel(
    var displayName: String?=null,
    var stockCode: String?=null,
    var stockName: String?=null
)