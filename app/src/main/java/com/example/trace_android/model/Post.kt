package com.example.trace_android.model

data class PostRequest(
    val content: String,
    val latitude: Double,
    val longitude: Double,
    val email: String
)

data class PostResponse(
    val message: String
)
