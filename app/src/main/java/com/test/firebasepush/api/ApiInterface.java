package com.test.firebasepush.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    /**
     * check first time or not
     * with reg no
     *
     * @param bean
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("send")
    Call<MessageResponse> sendFirebasePush(@Header("Authorization") String token, @Body JsonObject bean);


}