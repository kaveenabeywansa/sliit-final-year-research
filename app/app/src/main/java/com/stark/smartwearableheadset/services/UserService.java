package com.stark.smartwearableheadset.services;

import com.stark.smartwearableheadset.models.BlindUser;
import com.stark.smartwearableheadset.models.LoginCredentials;
import com.stark.smartwearableheadset.models.User;

import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @POST("users/login/")
    Call<ResponseBody> loginUser(@Body LoginCredentials credentials);

    @GET("users/blindlist/{id}/")
    Call<List<BlindUser>> getBlindUserListForAssociate(@Path("id") String username);

    @GET("users/{id}/")
    Call<BlindUser> getBlindUserDetails(@Path("id") String username);

    @GET("users/usertype/associate")
    Call<List<User>> getAssociateSearchList();

    @GET("users/searchassociate/{id}")
    Call<List<User>> getAssociateSearchList(@Path("id") String keyword);

    @POST("users/")
    Call<ResponseBody> registerNewUser(@Body User user);

    @POST("users/pwd/{id}")
    Call<ResponseBody> changePassword(@Path("id") String username, @Body LoginCredentials credentials);

    @GET("users/{id}/")
    Call<ResponseBody> getUserData(@Path("id") String username);

    @PUT("users/{id}")
    Call<ResponseBody> editUserProfile(@Path("id") String username, @Body BlindUser user);
}
