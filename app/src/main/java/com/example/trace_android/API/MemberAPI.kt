package com.example.trace_android.API

import com.example.trace_android.model.Member
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MemberAPI {
    @POST("/members/register")
    fun save(@Body member: Member): Call<Member>

    @GET("/members/{memberEmail}")
    fun getMemberByEmail(@Path("memberEmail") memberEmail: String): Call<Member>
}
