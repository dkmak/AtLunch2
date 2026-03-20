package com.atlunch.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlacePreviewEntity(
    @PrimaryKey val id: String,
    val restaurantName: String,
    val rating: Double?,
    val userRatingCount: Int?,
    val shortFormattedAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val iconBaseUri: String
)
