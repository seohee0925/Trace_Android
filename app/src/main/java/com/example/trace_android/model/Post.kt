package com.example.trace_android.model

import java.util.Date

data class PostRequest(
    val content: String,
    val latitude: Double,
    val longitude: Double,
    val email: String,
    val image: String, // 첫 번째 부분의 Base64 인코딩된 이미지 데이터
    val imageExtra: String? = null, // 추가 이미지 데이터
    val address: String,                 // 주소 필드 추가
    val createdDate: Date,
    val name: String
)

data class PostResponse(
    val message: String
)

// 서버에서 반환되는 포스트 데이터의 구조를 나타내는 클래스
data class Post(
    val id: Long,                        //포스팅의 id
    val email: String,                  //작성자의 Email
    val name: String,
    val content: String,
    val latitude: Double,
    val longitude: Double,
    val image: String,                  // 첫 번째 부분의 Base64 인코딩된 이미지 데이터
    val imageExtra: String?,             // 추가 이미지 데이터
    val address: String,               // 주소 필드 추가
    val createdDate: Date
)