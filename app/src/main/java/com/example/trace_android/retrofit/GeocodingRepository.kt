package com.example.trace_android.retrofit

import com.example.trace_android.BuildConfig
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GeocodingRepository(private val geocodingService: GeocodingService) {

    suspend fun getFormattedAddress(location: LatLng): String? = withContext(Dispatchers.IO) {
        try {
            val response = geocodingService.getGeocodedLocation(
                "${location.latitude},${location.longitude}",
                BuildConfig.MY_API_KEY // API 키를 여기에 넣으세요.
            )
            if (response.status == "OK" && response.results.isNotEmpty()) {
                return@withContext response.results[0].formattedAddress
            }
        } catch (e: Exception) {
            null
        }
        return@withContext null
    }
}
