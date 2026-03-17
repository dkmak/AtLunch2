package com.atlunch.domain

import com.atlunch.data.SearchNearbyRequest
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun searchNearby(
        lat: Double,
        long: Double
    ): Flow<List<PlacePreview>>
}