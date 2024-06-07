package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TarikBarangService {
    @GET("/apiMobile/list-expired")
    fun getListExpired(
        @Query("usp_user") uspUser: String,
        @Query("usp_dept") uspDept: String
    ): Call<List<TarikBarangModel>>
}
