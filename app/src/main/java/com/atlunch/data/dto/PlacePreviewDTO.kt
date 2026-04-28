package com.atlunch.data.dto

import com.atlunch.data.network.places.LatLngDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacePreviewDTO(
    val displayName: DisplayName,
    val id: String,
    val rating: Double? = null,
    val userRatingCount: Int? = null,
    val shortFormattedAddress: String? = null,
    @SerialName("location") val placeLocation: LatLngDTO? = null,
    @SerialName("iconMaskBaseUri") val iconBaseUri: String,
)

@Serializable
data class DisplayName(
    val text: String,
)
