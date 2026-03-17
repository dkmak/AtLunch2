package com.atlunch.data

import com.atlunch.data.dto.PlacePreviewDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

class PlacesApiClient @Inject constructor(
    val placesApiService: PlacesApiService
) {
    suspend fun searchNearby(
        request: SearchNearbyRequest
    ): SearchNearbyResponse {
        return placesApiService.searchNearby(
            apiKey = "REMOVED_GOOGLE_PLACES_KEY",
            fieldMask = SEARCH_NEARBY_FIELD_MASK,
            request = request
        )
    }

    companion object {
        const val SEARCH_NEARBY_FIELD_MASK =
            "places.displayName,places.id,places.rating,places.userRatingCount,places.shortFormattedAddress"
    }
}

interface PlacesApiService {
    @POST("./places:searchNearby")
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




