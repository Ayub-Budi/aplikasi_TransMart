package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.DaasboardExpiredModel
import com.a213310009ayubbudisantoso.transmart.api.model.DashboardResponse
import retrofit2.Call
import retrofit2.http.GET

interface DasboardEspiredService {
    @GET("/apiMobile/dashboard-didata")
    fun getDashboardData(): Call<DashboardResponse>
}

