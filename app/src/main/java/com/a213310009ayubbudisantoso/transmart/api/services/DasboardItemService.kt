package com.a213310009ayubbudisantoso.transmart.api.services

import DashboardExpiredModel
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DasboardItemService {
    @GET("/apiMobile/dashboard-expiringSoon")
    fun getListExpired(@Query("usp_user") uspUser: String): Call<List<DashboardExpiredModel>>

}