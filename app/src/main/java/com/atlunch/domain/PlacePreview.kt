package com.atlunch.domain

data class PlacePreview(
    val restaurantName: String,
    val id: String,
    val rating: Double?,
    val userRatingCount: Int?,
    val shortFormattedAddress: String?,
    val location: Location?,
    val iconBaseUri: String,
)
