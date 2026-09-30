package dkmak.atlunch.data.network.places

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BffPlacesApiService {
    @POST("proxy/google/v1/places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchNearbyRequest,
    ): SearchResultsResponse
}
