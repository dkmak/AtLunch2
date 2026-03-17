package com.atlunch.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacePreviewDTO(
    val displayName: DisplayName,
    val id: String,
    val rating: Double,
    val userRatingCount: Int,
    val shortFormattedAddress: String
)

@Serializable
data class DisplayName(
    val text: String
)