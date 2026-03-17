package com.atlunch

import com.atlunch.data.Circle
import com.atlunch.data.LatLng
import com.atlunch.data.LocationRestriction
import com.atlunch.data.PlacesApiClient
import com.atlunch.data.SearchNearbyRequest
import com.atlunch.data.dto.toDomain
import com.atlunch.data.toDomainError
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


class PlacesRepositoryImpl @Inject constructor(
    private val apiClient: PlacesApiClient
) : PlacesRepository {

    override fun searchNearby(
        lat: Double,
        long: Double
    ): Flow<List<PlacePreview>> = flow {
        val request = SearchNearbyRequest(
            includedTypes = listOf("restaurant"),
            maxResultCount = 10,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(40.728480, -73.982142), // this is for Westville
                    radius = 500.0
                )
            )
        )
        val response = apiClient.searchNearby(request)
        emit(response.places.map { placePreviewDTO -> placePreviewDTO.toDomain() })
    }. catch { throwable ->
        throw throwable.toDomainError() // I'm not sure how I would never map an error to the viewModel
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindPlacesRepository(placesRepositoryImpl: PlacesRepositoryImpl): PlacesRepository
}
