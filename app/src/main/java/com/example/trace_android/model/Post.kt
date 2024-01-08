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

// 서버에서 반환되는 포스트 데이터의 구조를 나타내는 클래스
data class Post(
    val id: Int,                        //포스팅의 id
    val email: String,                  //작성자의 Email
    val content: String,
    val latitude: Double,
    val longitude: Double,
    val image: String,                  // 첫 번째 부분의 Base64 인코딩된 이미지 데이터
    val imageExtra: String?      // 추가 이미지 데이터 (필요한 경우)
    // 기타 필요한 필드들...
)