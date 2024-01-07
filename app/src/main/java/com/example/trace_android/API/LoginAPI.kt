package com.example.trace_android.API

import com.example.trace_android.model.Member
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginAPI {
    @FormUrlEncoded
    @POST("/members/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Member>
}