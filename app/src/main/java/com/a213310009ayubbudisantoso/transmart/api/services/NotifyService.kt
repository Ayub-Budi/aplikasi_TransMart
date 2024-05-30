package com.a213310009ayubbudisantoso.transmart.api.service

import com.a213310009ayubbudisantoso.transmart.api.model.StoreNotifyModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NotifyService {
    @GET("/apiMobile/notify-expired")
    fun getExpiredNotifications(@Query("usp_user") uspUser: String): Call<List<StoreNotifyModel>>
}

