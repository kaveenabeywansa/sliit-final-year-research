package com.stark.smartwearableheadset.services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    private static String baseUrl = "http://34.69.14.141/"; // new cloud hosted ip
//        private static String baseUrl = "http://35.224.75.243/"; // old cloud hosted ip
//        private static String baseUrl = "http://192.168.43.223/"; // testing at mobile
//    private static String baseUrl = "http://192.168.1.102/"; // testing at home

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
