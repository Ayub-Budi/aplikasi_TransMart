package com.a213310009ayubbudisantoso.transmart.api.model

import com.google.gson.annotations.SerializedName

data class ItemNotifyModel(
    val department: Int?,
    @SerializedName("itemName") val itemName: String?,
    @SerializedName("expirationDate") val expirationDate: String?,
    val wording: String?,
    @SerializedName("ie_id") val ieId: String?,
    @SerializedName("gondolaNo") val gondolaNo: Int?,
    @SerializedName("itemCode") val itemCode: String?,
    val status: String?,
    val quantity: Int?
)
