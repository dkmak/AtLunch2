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
        query: String,
        lat: Double,
        long: Double
    ): Flow<PlacesResult>


    fun addFavorite(
        id: String
    ): Flow<FavoriteResult>

    fun removeFavorite(
        id: String
    ): Flow<FavoriteResult>
}