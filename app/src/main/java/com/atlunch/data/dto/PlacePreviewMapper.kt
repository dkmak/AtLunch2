package com.atlunch.data.dto

import com.atlunch.data.database.PlacePreviewEntity
import com.atlunch.domain.Location
import com.atlunch.domain.PlacePreview

fun PlacePreviewDTO.toDomain(): PlacePreview {
    return PlacePreview(
        restaurantName = this.displayName.text,
        id = this.id,
        rating = this.rating,
        userRatingCount = this.userRatingCount,
        shortFormattedAddress = this.shortFormattedAddress,
        location = this.placeLocation?.toDomain(),
        iconBaseUri = "${this.iconBaseUri}.png"
    )
}

fun PlacePreviewDTO.toEntity(): PlacePreviewEntity {
    return PlacePreviewEntity(
        id = this.id,
        restaurantName = this.displayName.text,
        rating = this.rating,
        userRatingCount = this.userRatingCount,
        shortFormattedAddress = this.shortFormattedAddress,
        latitude = this.placeLocation?.latitude,
        longitude = this.placeLocation?.longitude,
        iconBaseUri = "${this.iconBaseUri}.png"
    )
}

fun PlacePreviewEntity.toDomain(): PlacePreview {
    return PlacePreview(
        restaurantName = this.restaurantName,
        id = this.id,
        rating = this.rating,
        userRatingCount = this.userRatingCount,
        shortFormattedAddress = this.shortFormattedAddress,
        location = if (this.latitude != null && this.longitude != null) {
            Location(
                latitude = this.latitude,
                longitude = this.longitude
            )
        } else {
            null
        },
        iconBaseUri = this.iconBaseUri
    )
}
