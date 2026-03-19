package com.atlunch.data.repository

import android.annotation.SuppressLint
import android.location.Location
import com.atlunch.domain.LocationRepository
import com.atlunch.domain.LocationResult
import com.atlunch.domain.UserLocation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationResult {
        return try {
            val currentLocation: Location? = suspendCancellableCoroutine { continuation ->
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .build()

                fusedLocationClient
                    .getCurrentLocation(request, null)
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.failure(error))
                        }
                    }

                continuation.invokeOnCancellation {
                    // Play Services Tasks do not support cooperative cancellation here.
                }
            }

            val resolvedLocation: Location? = currentLocation ?: suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.failure(error))
                        }
                    }
            }

            if (resolvedLocation == null) {
                LocationResult.LocationError.Unknown
            } else {
                LocationResult.LocationSuccess(
                    userLocation = UserLocation(
                        latitude = resolvedLocation.latitude,
                        longitude = resolvedLocation.longitude
                    )
                )
            }
        } catch (e : Exception) {
            LocationResult.LocationError.Unknown
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocationRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}
