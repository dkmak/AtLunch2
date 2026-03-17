package com.atlunch.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

class ApiClient @Inject constructor(
    val apiService: ApiService
) {
    suspend fun searchNearby(
        request: SearchNearbyRequest
    ): SearchNearbyResponse {
        return apiService.searchNearby(
            apiKey = "",
            fieldMask = "SEARCH_NEARBY_FIELD_MASK",
            request = request
        )
    }

    companion object {
        const val SEARCH_NEARBY_FIELD_MASK =
            "places.name,places.id,places.rating,places.userRatingCount,places.shortFormattedAddress"
    }
}

interface ApiService {
    @POST("places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchNearbyRequest
    ): SearchNearbyResponse
}

@Serializable
data class SearchNearbyResponse(
    val places: List<PlacePreviewDTO>
)

@Serializable
data class SearchNearbyRequest(
    val includedTypes: List<String>,
    val maxResultCount: Int,
    val locationRestriction: LocationRestriction
)

@Serializable
data class LocationRestriction(
    val circle: Circle
)

@Serializable
data class Circle(
    val center: LatLng,
    val radius: Double
)

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class PlacePreviewDTO(
    @SerialName("name") val restaurantName: String, // consider updating to displayName
    val id: String,
    val rating: Double,
    val userRatingCount: Int,
    val shortFormattedAddress: String
)



