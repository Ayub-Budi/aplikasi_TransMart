package com.a213310009ayubbudisantoso.transmart.api.model

import com.google.gson.annotations.SerializedName
import java.util.Date

class BebasExpiredModel {
    @SerializedName("gondala_number")
    var gondalaNumber: String? = null

    @SerializedName("item_code")
    var itemCode: String? = null

    @SerializedName("item_name")
    var itemName: String? = null

    @SerializedName("status_item")
    var statusItem: String? = null

    @SerializedName("expired_date")
    var expiredDate: String? = null

    @SerializedName("item_amount")
    var itemAmount: Int? = null

    @SerializedName("icone_plane")
    var iconePlane: String? = null

    @SerializedName("create_by")
    var createBy: String? = null

    @SerializedName("update_by")
    var updateBy: String? = null

    @SerializedName("store_code")
    var storecode: Int? = null
}
