package com.atlunch.data.network

import com.atlunch.data.dto.PlaceDetailsDTO
import com.atlunch.data.dto.PlacePreviewDTO
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject

class PlacesApiClient @Inject constructor(
    val placesApiService: PlacesApiService
) {
    suspend fun searchNearby(
        request: SearchNearbyRequest
    ): SearchNearbyResponse {
        return placesApiService.searchNearby(
            apiKey = "REMOVED_GOOGLE_PLACES_KEY", // TODO HIDE THIS
            fieldMask = SEARCH_NEARBY_FIELD_MASK,
            request = request
        )
    }

    suspend fun getPlaceDetails(
        id: String
    ): PlaceDetailsDTO {
        return placesApiService.getPlaceDetails(
            apiKey = "REMOVED_GOOGLE_PLACES_KEY", // TODO HIDE THIS
            fieldMask = GET_DETAILS_FIELD_MASK,
            id = id
        )
    }

    companion object {
        const val SEARCH_NEARBY_FIELD_MASK = // TODO CHECK ON THIS
            "places.displayName,places.id,places.rating,places.userRatingCount,places.shortFormattedAddress"
        const val GET_DETAILS_FIELD_MASK =
            "displayName,id,rating,userRatingCount,formattedAddress,nationalPhoneNumber"
    }
}

interface PlacesApiService {
    @POST("/v1/places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchNearbyRequest
    ): SearchNearbyResponse

    @GET("/v1/places/{id}")
    suspend fun getPlaceDetails(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Path("id") id: String
    ): PlaceDetailsDTO
}


@Serializable
data class SearchNearbyResponse(
    val places: List<PlacePreviewDTO>
)


