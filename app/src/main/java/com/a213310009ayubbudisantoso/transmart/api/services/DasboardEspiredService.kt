package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.DashboardResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DasboardEspiredService {
    @GET("/apiMobile/dashboard-didata")
    fun getDashboardData(
        @Query("usp_user") uspUser: String,
        @Query("usp_dept") uspDept: String
    ): Call<DashboardResponse>
    // fun getDashboardData(@Query("usp_user") uspUser: String): Call<List<DashboardResponse>>
}

