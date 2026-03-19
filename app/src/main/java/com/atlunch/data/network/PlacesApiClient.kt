package com.atlunch.data.network

import com.atlunch.data.dto.PhotoMediaDTO
import com.atlunch.data.dto.PlaceDetailsDTO
import com.atlunch.data.dto.PlacePreviewDTO
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

class PlacesApiClient @Inject constructor(
    val placesApiService: PlacesApiService
) {
    suspend fun searchNearby(
        request: SearchNearbyRequest
    ): SearchResultsResponse {
        return placesApiService.searchNearby(
            apiKey = API_KEY,
            fieldMask = SEARCH_RESULTS_FIELD_MASK,
            request = request
        )
    }

    suspend fun getPlaceDetails(
        id: String
    ): PlaceDetailsDTO {
        return placesApiService.getPlaceDetails(
            apiKey = API_KEY,
            fieldMask = GET_DETAILS_FIELD_MASK,
            id = id
        )
    }

    suspend fun searchQuery(
        request: SearchQueryRequest
    ): SearchResultsResponse {
        return placesApiService.searchQuery(
            apiKey = API_KEY,
            fieldMask = SEARCH_RESULTS_FIELD_MASK,
            request = request
        )
    }

    suspend fun getPhotos(name: String): PhotoMediaDTO {
        return placesApiService.getPhotoMedia(
            name = "$name/media",
            key = API_KEY,
            maxHeightPx = 400,
            maxWidthPx = 400,
            skipHttpRedirect = true
        )
    }

    companion object {
        const val API_KEY = "REMOVED_GOOGLE_PLACES_KEY"
        const val SEARCH_RESULTS_FIELD_MASK =
            "places.displayName,places.id,places.rating,places.userRatingCount,places.shortFormattedAddress,places.iconMaskBaseUri"
        const val GET_DETAILS_FIELD_MASK =
            "displayName,id,rating,userRatingCount,formattedAddress,nationalPhoneNumber,photos"
    }
}

interface PlacesApiService {
    @POST("/v1/places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchNearbyRequest
    ): SearchResultsResponse

    @GET("/v1/places/{id}")
    suspend fun getPlaceDetails(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Path("id") id: String
    ): PlaceDetailsDTO

    @POST("/v1/places:searchText")
    suspend fun searchQuery(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchQueryRequest
    ): SearchResultsResponse

    @GET("/v1/{name}")
    suspend fun getPhotoMedia(
        @Path(value = "name", encoded = true) name: String,
        @Query("key") key: String,
        @Query("maxHeightPx") maxHeightPx: Int? = null,
        @Query("maxWidthPx") maxWidthPx: Int? = null,
        @Query("skipHttpRedirect") skipHttpRedirect: Boolean = true
    ): PhotoMediaDTO
}


@Serializable
data class SearchResultsResponse(
    val places: List<PlacePreviewDTO> = emptyList()
)

