package com.a213310009ayubbudisantoso.transmart.api.model

data class User(
    val nik: String,
    val name: String,
    val email: String,
    val oprCode: String?,
    val locationCode: String?,
    val locationName: String?,
    val profile: String
)
