package com.example.trace_android.model

data class PostRequest(
    val content: String,
    val latitude: Double,
    val longitude: Double,
    val email: String,
    val image: String, // 첫 번째 부분의 Base64 인코딩된 이미지 데이터
    val imageExtra: String? = null // 추가 이미지 데이터 (필요한 경우)
)

data class PostResponse(
    val message: String
)
