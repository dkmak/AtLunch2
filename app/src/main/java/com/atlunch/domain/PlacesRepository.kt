package com.atlunch.domain

import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun searchNearby(
        lat: Double,
        long: Double
    ): Flow<List<PlacePreview>>

    fun getPlaceDetails(
        id: String
    ):Flow<PlaceDetails>
}