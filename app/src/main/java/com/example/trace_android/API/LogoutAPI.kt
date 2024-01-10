package com.example.trace_android.API

import retrofit2.Call
import retrofit2.http.POST

interface LogoutAPI {
    @POST("/members/logout")
    fun logout(): Call<Void>
}
