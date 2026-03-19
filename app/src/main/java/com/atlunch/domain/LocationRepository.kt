package com.atlunch.domain

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
}
