package dkmak.atlunch.domain

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
}
