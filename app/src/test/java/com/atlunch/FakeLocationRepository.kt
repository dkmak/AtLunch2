package com.atlunch

import com.atlunch.domain.LocationRepository
import com.atlunch.domain.LocationResult
import kotlinx.coroutines.test.StandardTestDispatcher

class FakeLocationRepository : LocationRepository{
    var locationResult: LocationResult = LocationResult.LocationError.Unknown

    override suspend fun getCurrentLocation(): LocationResult {
        return locationResult
    }
}