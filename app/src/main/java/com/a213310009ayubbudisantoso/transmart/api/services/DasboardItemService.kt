package com.a213310009ayubbudisantoso.transmart.api.services

import DashboardExpiredModel
import retrofit2.Call
import retrofit2.http.GET

interface DasboardItemService {
    @GET("/apiMobile/dashboard-expiringSoon")
    fun getListExpired(): Call<List<DashboardExpiredModel>>
}