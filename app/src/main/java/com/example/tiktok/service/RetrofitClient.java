package com.example.tiktok.service;

import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static retrofit2.Retrofit instance;
    static String baseUrl = "https://nodejs-mysql-1sml.onrender.com";
    public static retrofit2.Retrofit getInstance(){
        if(instance == null){
            instance = new retrofit2.Retrofit.Builder()
                    .baseUrl(baseUrl) //domain của api
                    .addConverterFactory(GsonConverterFactory.create()) //chuyển đổi JSON thành đối tượng java
                    // .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
}
