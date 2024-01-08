package com.example.trace_android.API

import com.example.trace_android.model.Post
import com.example.trace_android.model.PostRequest
import com.example.trace_android.model.PostResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @POST("posts/create")
    suspend fun createPost(@Body postRequest: PostRequest): PostResponse

    // 지정된 범위 내의 포스트들을 가져오는 API
    // 화면 남서쪽의 점의 좌표, 화면 북서쪽 점의 좌표를 전달하고
    // 포스트 데이터들을 리스트로 받아옴.
    @GET("/posts/inArea")
    suspend fun getPostsInArea(
        @Query("southWestLat") southWestLat: Double,
        @Query("southWestLng") southWestLng: Double,
        @Query("northEastLat") northEastLat: Double,
        @Query("northEastLng") northEastLng: Double
    ): Response<List<Post>>

}