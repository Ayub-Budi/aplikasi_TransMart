package com.a213310009ayubbudisantoso.transmart.api.model
import com.google.gson.annotations.SerializedName

class LoginModel {
    @SerializedName("username")
    var user: String? = null

    @SerializedName("passwd")
    var pswd: String? = null

}