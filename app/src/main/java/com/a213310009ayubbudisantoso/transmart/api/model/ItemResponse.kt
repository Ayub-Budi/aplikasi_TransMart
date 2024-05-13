package com.a213310009ayubbudisantoso.transmart.api.model

import com.google.gson.annotations.SerializedName

//data class ItemResponse(
//    val data: String
//)
data class ItemResponse(
    val barcode: String,
    val item_desc: String,
    val item_code: String,
    val storecode: String,
    val storename: String,
    val refunable: String
)
//data class MasterItemResponse(val data: String)
