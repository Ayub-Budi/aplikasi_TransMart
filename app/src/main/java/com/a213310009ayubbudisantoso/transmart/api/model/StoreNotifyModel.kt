package com.a213310009ayubbudisantoso.transmart.api.model

data class StoreNotifyModel(
    val storeCode: String,
    val storeName: String,
    val items: List<ItemNotifyModel>
)
