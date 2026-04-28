package com.atlunch

import com.atlunch.domain.LocationRepository
import com.atlunch.domain.LocationResult

class FakeLocationRepository : LocationRepository {
    var locationResult: LocationResult = LocationResult.LocationError.Unknown
    var getCurrentLocationCallCount: Int = 0
        private set

    override suspend fun getCurrentLocation(): LocationResult {
        getCurrentLocationCallCount++
        return locationResult
    }
}
