package com.a213310009ayubbudisantoso.transmart.api.services;

import com.a213310009ayubbudisantoso.transmart.api.model.BebasExpiredModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BebasExpiredService {
    @POST("/api/detail-item")
    Call<Void> postData(@Body BebasExpiredModel data);
}
