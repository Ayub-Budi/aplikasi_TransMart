package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import retrofit2.Call
import retrofit2.http.GET

interface TarikBarangService {
    @GET("/apiMobile/list-expired")
    fun getListExpired(): Call<List<TarikBarangModel>>
}
