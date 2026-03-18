package com.atlunch.domain

import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun searchNearby(
        lat: Double,
        long: Double
    ): Flow<PlacesResult>

    fun getPlaceDetails(
        id: String
    ): Flow<PlaceDetailsResult>

    fun searchQuery(
        query: String
    ): Flow<PlacesResult>
}