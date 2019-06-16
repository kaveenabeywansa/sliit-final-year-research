package com.stark.smartwearableheadset.services;

import com.stark.smartwearableheadset.models.RealTimeStat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StatsService {
    @POST("stats/")
    Call<ResponseBody> loginUser(@Body RealTimeStat realTimeStat);
}
