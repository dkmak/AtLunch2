package com.atlunch.data.dto

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
