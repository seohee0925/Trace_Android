package com.example.trace_android.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {

    lateinit var retrofit: Retrofit

    init {
        initializeRetrofit()
    }

    private fun initializeRetrofit() {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl("http://143.248.225.149:8080")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}
