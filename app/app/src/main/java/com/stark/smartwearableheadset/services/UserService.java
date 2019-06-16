package com.stark.smartwearableheadset.services;

import com.stark.smartwearableheadset.models.LoginCredentials;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("users/login/")
    Call<ResponseBody> loginUser(@Body LoginCredentials credentials);
}
