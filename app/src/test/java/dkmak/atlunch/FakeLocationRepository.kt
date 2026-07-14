package dkmak.atlunch

import dkmak.atlunch.domain.LocationRepository
import dkmak.atlunch.domain.LocationResult

class FakeLocationRepository : LocationRepository {
    var locationResult: LocationResult = LocationResult.LocationError.Unknown
    var getCurrentLocationCallCount: Int = 0
        private set

    override suspend fun getCurrentLocation(): LocationResult {
        getCurrentLocationCallCount++
        return locationResult
    }
}
