package com.atlunch.domain

import kotlinx.serialization.SerialName

data class PlacePreview (
    val restaurantName: String,
    val id: String,
    val rating: Double,
    val userRatingCount: Int,
    val shortFormattedAddress: String,
    val iconBaseUri: String
)