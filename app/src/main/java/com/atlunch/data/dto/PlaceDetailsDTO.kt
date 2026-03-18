package com.atlunch.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsDTO(
    val displayName: DisplayName,
    val id: String,
    val rating: Double,
    val userRatingCount: Int,
    val formattedAddress: String,
    val nationalPhoneNumber: String? = null
)
