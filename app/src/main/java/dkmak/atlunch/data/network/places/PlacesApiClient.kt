package dkmak.atlunch.data.network.places

import dkmak.atlunch.data.dto.PhotoMediaDTO
import dkmak.atlunch.data.dto.PlaceDetailsDTO
import dkmak.atlunch.data.dto.PlacePreviewDTO
import dkmak.atlunch.data.network.bff.BffPlacesApiService
import kotlinx.serialization.Serializable
import javax.inject.Inject

class PlacesApiClient
    @Inject
    constructor(
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
            bffPlacesApiService.getPhotoMedia(
                name = name,
                maxHeightPx = 400,
                maxWidthPx = 400,
            )

        companion object {
            const val SEARCH_RESULTS_FIELD_MASK =
                "places.displayName,places.id,places.rating,places.userRatingCount,places.shortFormattedAddress,places.iconMaskBaseUri,places.location"
            const val GET_DETAILS_FIELD_MASK =
                "displayName,id,rating,userRatingCount,googleMapsUri,formattedAddress,nationalPhoneNumber,photos,regularOpeningHours"
        }
    }

@Serializable
data class SearchResultsResponse(
    val places: List<PlacePreviewDTO> = emptyList(),
)
