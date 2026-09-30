package dkmak.atlunch.data.network.bff

import dkmak.atlunch.data.network.places.SearchNearbyRequest
import dkmak.atlunch.data.network.places.SearchQueryRequest
import dkmak.atlunch.data.network.places.SearchResultsResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BffPlacesApiService {
    @POST("proxy/google/v1/places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchNearbyRequest,
    ): SearchResultsResponse

    @POST("proxy/google/v1/places:searchText")
    suspend fun searchQuery(
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchQueryRequest,
    ): SearchResultsResponse
}
