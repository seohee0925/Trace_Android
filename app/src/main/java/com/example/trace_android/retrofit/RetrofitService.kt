package com.example.trace_android.retrofit

import com.example.trace_android.API.ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService { // 싱글톤 객체로 변경

    val retrofit: Retrofit by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            //.baseUrl("http://143.248.225.134:8080")   // 서희
            .baseUrl("http://143.248.218.51:8080")    // 재용
            //.baseUrl("http://172.10.7.70:80")         // VM
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }


    // Geocoding API 서비스
    private val geocodingRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geocodingService: GeocodingService by lazy {
        geocodingRetrofit.create(GeocodingService::class.java)
    }

}
