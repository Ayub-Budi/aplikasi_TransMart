package com.a213310009ayubbudisantoso.transmart.api.services;

import com.a213310009ayubbudisantoso.transmart.api.model.BebasExpiredModel;
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BebasExpiredService {
    @POST("/apiMobile/save-item")
    Call<Void> postData(@Body BebasExpiredModel data);
}