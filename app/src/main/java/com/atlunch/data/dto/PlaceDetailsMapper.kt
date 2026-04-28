package com.atlunch.data.dto

import com.atlunch.domain.PlaceDetails

fun PlaceDetailsDTO.toDomain(): PlaceDetails =
    PlaceDetails(
        restaurantName = this.displayName.text,
        id = this.id,
        rating = this.rating,
        googleMapsUri = this.googleMapsUri,
        userRatingCount = this.userRatingCount,
        formattedAddress = this.formattedAddress,
        nationalPhoneNumber = this.nationalPhoneNumber,
        openingHours = this.openingHours?.weekdayDescriptions,
    )
