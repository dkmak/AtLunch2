package com.atlunch.domain


data class PlaceDetails (
    val restaurantName: String,
    val id: String,
    val rating: Double?,
    val userRatingCount: Int?,
    val formattedAddress: String?,
    val nationalPhoneNumber: String?,
    val openingHours: List<String>?
)
