package com.a213310009ayubbudisantoso.transmart.api.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("data_listed") val dataListed: DashboardData,
    @SerializedName("data_withdrawn") val dataWithdrawn: DashboardData
)
