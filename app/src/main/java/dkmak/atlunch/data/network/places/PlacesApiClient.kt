package dkmak.atlunch.data.network.places

import dkmak.atlunch.BuildConfig
import dkmak.atlunch.data.dto.PhotoMediaDTO
import dkmak.atlunch.data.dto.PlaceDetailsDTO
import dkmak.atlunch.data.dto.PlacePreviewDTO
import dkmak.atlunch.data.network.bff.BffPlacesApiService
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

class PlacesApiClient
    @Inject
    constructor(
        val placesApiService: PlacesApiService,
        private val bffPlacesApiService: BffPlacesApiService,
    ) {
        suspend fun searchNearby(request: SearchNearbyRequest): SearchResultsResponse =
            bffPlacesApiService.searchNearby(
                fieldMask = SEARCH_RESULTS_FIELD_MASK,
                request = request,
            )

        suspend fun getPlaceDetails(id: String): PlaceDetailsDTO =
            bffPlacesApiService.getPlaceDetails(
                fieldMask = GET_DETAILS_FIELD_MASK,
                id = id,
            )

        suspend fun searchQuery(request: SearchQueryRequest): SearchResultsResponse =
            bffPlacesApiService.searchQuery(
                fieldMask = SEARCH_RESULTS_FIELD_MASK,
                request = request,
            )

        suspend fun getPhotos(name: String): PhotoMediaDTO =
            placesApiService.getPhotoMedia(
                name = "$name/media",
                key = API_KEY,
                maxHeightPx = 400,
                maxWidthPx = 400,
                skipHttpRedirect = true,
            )

        companion object {
            const val API_KEY = BuildConfig.GOOGLE_PLACES_API_KEY
            const val SEARCH_RESULTS_FIELD_MASK =
                "places.displayName,places.id,places.rating,places.userRatingCount,places.shortFormattedAddress,places.iconMaskBaseUri,places.location"
            const val GET_DETAILS_FIELD_MASK =
                "displayName,id,rating,userRatingCount,googleMapsUri,formattedAddress,nationalPhoneNumber,photos,regularOpeningHours"
        }
    }

interface PlacesApiService {
    @GET("/v1/{name}")
    suspend fun getPhotoMedia(
        @Path(value = "name", encoded = true) name: String,
        @Query("key") key: String,
        @Query("maxHeightPx") maxHeightPx: Int? = null,
        @Query("maxWidthPx") maxWidthPx: Int? = null,
        @Query("skipHttpRedirect") skipHttpRedirect: Boolean = true,
    ): PhotoMediaDTO
}

@Serializable
data class SearchResultsResponse(
    val places: List<PlacePreviewDTO> = emptyList(),
)
