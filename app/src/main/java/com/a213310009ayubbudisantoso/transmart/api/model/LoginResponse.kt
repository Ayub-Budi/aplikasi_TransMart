package com.a213310009ayubbudisantoso.transmart.api.model

data class LoginResponse(
    val apiData: ApiData,
    val dbData: List<DbData>
)