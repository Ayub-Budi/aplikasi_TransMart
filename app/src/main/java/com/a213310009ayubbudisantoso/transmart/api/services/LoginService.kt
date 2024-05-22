package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.LoginResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("apiMobile/login")
    fun login(@Body body: JsonObject): Call<LoginResponse>
}