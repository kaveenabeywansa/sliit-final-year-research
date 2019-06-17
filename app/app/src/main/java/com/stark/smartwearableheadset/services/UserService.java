package com.stark.smartwearableheadset.services;

import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.models.LoginCredentials;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @POST("users/login/")
    Call<ResponseBody> loginUser(@Body LoginCredentials credentials);

    @GET("users/blindlist/{id}/")
    Call<List<BlindUser>> getBlindUserListForAssociate(@Path("id") String username);

    @GET("users/{id}/")
    Call<BlindUser> getBlindUserDetails(@Path("id") String username);
}
