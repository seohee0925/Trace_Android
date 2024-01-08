package com.example.trace_android.model

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<AddressResult>,
    val status: String
)

data class AddressResult(
    @SerializedName("formatted_address") // 이 부분을 추가
    val formattedAddress: String
)
