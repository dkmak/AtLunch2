package com.atlunch.data.dto

import com.atlunch.domain.Photo
import com.atlunch.domain.PlaceDetails

fun PlaceDetailsDTO.toDomain(): PlaceDetails {
    return PlaceDetails(
        restaurantName = this.displayName.text,
        id = this.id,
        rating = this.rating,
        userRatingCount = this.userRatingCount,
        formattedAddress = this.formattedAddress,
        nationalPhoneNumber = this.nationalPhoneNumber.orEmpty()
    )
}