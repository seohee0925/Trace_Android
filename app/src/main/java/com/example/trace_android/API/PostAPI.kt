package com.example.trace_android.API

import com.example.trace_android.model.PostRequest
import com.example.trace_android.model.PostResponse
import retrofit2.http.POST
import retrofit2.http.Body
interface ApiService {
    @POST("posts/create")
    suspend fun createPost(@Body postRequest: PostRequest): PostResponse
}