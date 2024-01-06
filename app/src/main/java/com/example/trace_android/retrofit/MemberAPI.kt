package com.example.trace_android.retrofit

import com.example.trace_android.model.Member
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberAPI {
    @POST("/members/register")
    fun save(@Body member: Member): Call<Member>
}
