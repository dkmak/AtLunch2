package com.atlunch.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsDTO(
    val displayName: DisplayName,
    val id: String,
    val rating: Double?,
    val googleMapsUri: String?,
    val userRatingCount: Int?,
    val formattedAddress: String?,
    val photos: List<PhotoResourceDTO> = emptyList(),
    val nationalPhoneNumber: String? = null,
    @SerialName("regularOpeningHours") val openingHours: OpeningHours? = null,
)

@Serializable
data class OpeningHours(
    val weekdayDescriptions: List<String>,
)
