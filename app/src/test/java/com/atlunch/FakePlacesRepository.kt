package com.atlunch

import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.PlacesResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePlacesRepository : PlacesRepository {
    var placeDetailsResult: PlaceDetailsResult = PlaceDetailsResult.DetailsError.Unknown
    var nearbyResult: PlacesResult = PlacesResult.PlacesError.Unknown
    var queryResult: PlacesResult = PlacesResult.PlacesError.Unknown

    var lastRequestedPlaceId: String? = null
        private set
    var lastSearchQuery: String? = null
        private set
    var lastNearbyLat: Double? = null
        private set
    var lastNearbyLong: Double? = null
        private set

    override fun searchNearby(
        lat: Double,
        long: Double
    ): Flow<PlacesResult> {
        lastNearbyLat = lat
        lastNearbyLong = long
        return flowOf(nearbyResult)
    }

    override fun getPlaceDetails(id: String): Flow<PlaceDetailsResult> {
        lastRequestedPlaceId = id
        return flowOf(placeDetailsResult)
    }

    override fun searchQuery(
        query: String,
        lat: Double,
        long: Double
    ): Flow<PlacesResult> {
        lastSearchQuery = query
        lastNearbyLat = lat
        lastNearbyLong = long
        return flowOf(queryResult)
    }
}
