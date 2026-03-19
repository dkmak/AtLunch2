package com.atlunch.data.network

import kotlinx.serialization.Serializable

@Serializable
data class SearchNearbyRequest(
    val includedTypes: List<String>,
    val maxResultCount: Int,
    val locationRestriction: LocationRestriction
)

@Serializable
data class LocationRestriction(
    val circle: Circle
)

@Serializable
data class Circle(
    val center: LatLngDTO,
    val radius: Double
)

@Serializable
data class LatLngDTO(
    val latitude: Double,
    val longitude: Double
)
