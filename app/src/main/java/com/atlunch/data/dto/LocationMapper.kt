package com.atlunch.data.dto

import com.atlunch.data.network.LatLngDTO
import com.atlunch.domain.Location

fun LatLngDTO.toDomain(): Location {
    return Location(
        latitude = this.latitude,
        longitude = this.longitude
    )
}