package com.a213310009ayubbudisantoso.transmart.api.services

import com.a213310009ayubbudisantoso.transmart.api.model.LoginModel
import com.a213310009ayubbudisantoso.transmart.api.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/aso/checklogin.php")
    fun postData(@Body data: LoginModel): Call<UserResponse>
}

