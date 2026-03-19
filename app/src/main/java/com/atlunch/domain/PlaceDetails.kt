package com.atlunch.domain

import com.atlunch.data.dto.DisplayName

data class PlaceDetails (
    val restaurantName: String,
    val id: String,
    val rating: Double?,
    val userRatingCount: Int?,
    val formattedAddress: String?,
    // val photos: List<Photo>,
    val nationalPhoneNumber: String?
)
