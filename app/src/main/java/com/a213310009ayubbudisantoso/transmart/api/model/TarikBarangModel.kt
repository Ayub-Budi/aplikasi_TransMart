package com.a213310009ayubbudisantoso.transmart.api.model

import com.google.gson.annotations.SerializedName

class TarikBarangModel(
    @SerializedName("gondala_number") var gondalaNumber: String?,
    @SerializedName("item_code") var itemCode: String?,
    @SerializedName("item_name") var itemName: String?,
    @SerializedName("status_item") var statusItem: String?,
    @SerializedName("expired_date") var expiredDate: String?,
    @SerializedName("item_amount") var itemAmount: Int?,
    @SerializedName("icone_plane") var iconePlane: String?,
    @SerializedName("create_by") var createBy: String?,
    @SerializedName("update_by") var updateBy: String?,
    @SerializedName("store_code") var storeCode: String?
) {
    override fun toString(): String {
        return "TarikBarangModel(gondalaNumber=$gondalaNumber, itemCode=$itemCode, itemName=$itemName, statusItem=$statusItem, expiredDate=$expiredDate, itemAmount=$itemAmount, iconePlane=$iconePlane, createBy=$createBy, updateBy=$updateBy, storeCode=$storeCode)"
    }
}
