package com.stark.smartwearableheadset.services;

import com.stark.smartwearableheadset.models.RealTimeStat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StatsService {
    @POST("stats/")
    Call<ResponseBody> loginUser(@Body RealTimeStat realTimeStat);

    @GET("stats/latest/{id}/")
    Call<ResponseBody> getLatestStats(@Path("id") String username);
}
