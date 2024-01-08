package com.example.trace_android.retrofit

import com.example.trace_android.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("maps/api/geocode/json")
    suspend fun getGeocodedLocation(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}