package com.example.trace_android.model

import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)