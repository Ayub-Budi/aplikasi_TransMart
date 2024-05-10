package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.DaasboardExpiredModel
import retrofit2.Call
import retrofit2.http.GET

interface DasboardEspiredService {
    @GET("/apiMobile/dashboard-expiringSoon")
    fun getListExpired(): Call<List<DaasboardExpiredModel>>
}