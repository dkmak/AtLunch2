package dkmak.atlunch.data.dto

import dkmak.atlunch.data.network.places.LatLngDTO
import dkmak.atlunch.domain.Location

fun LatLngDTO.toDomain(): Location =
    Location(
        latitude = this.latitude,
        longitude = this.longitude,
    )
