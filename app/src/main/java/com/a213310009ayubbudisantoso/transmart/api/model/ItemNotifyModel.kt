package com.a213310009ayubbudisantoso.transmart.api.model

data class ItemNotifyModel(
    val department: String,
    val itemName: String,
    val expirationDate: String,
    val wording: String,
    val ie_id: String,
    val gondolaNo: String,
    val barcode: String,
    val quantity: Int,
)
