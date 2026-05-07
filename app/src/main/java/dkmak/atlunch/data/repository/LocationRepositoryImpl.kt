package dkmak.atlunch.data.repository

import android.annotation.SuppressLint
import dkmak.atlunch.data.IoDispatcher
import dkmak.atlunch.domain.Location
import dkmak.atlunch.domain.LocationRepository
import dkmak.atlunch.domain.LocationResult
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class LocationRepositoryImpl
    @Inject
    constructor(
        private val fusedLocationClient: FusedLocationProviderClient,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : LocationRepository {
        @SuppressLint("MissingPermission")
        override suspend fun getCurrentLocation(): LocationResult =
            withContext(ioDispatcher) {
                return@withContext try {
                    val request =
                        CurrentLocationRequest
                            .Builder()
                            .setPriority(PRIORITY_BALANCED_POWER_ACCURACY)
                            .build()

                    val location = fusedLocationClient.getCurrentLocation(request, null).await()

                    if (location == null) {
                        LocationResult.LocationError.Unknown
                    } else {
                        LocationResult.LocationSuccess(
                            location =
                                Location(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                ),
                        )
                    }
                } catch (_: Exception) {
                    LocationResult.LocationError.Unknown
                }
            }
    }

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocationRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
}
