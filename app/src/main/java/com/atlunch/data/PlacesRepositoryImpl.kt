package com.atlunch.data

import com.atlunch.data.dto.toDomain
import com.atlunch.data.network.Circle
import com.atlunch.data.network.LatLng
import com.atlunch.data.network.LocationBias
import com.atlunch.data.network.LocationRestriction
import com.atlunch.data.network.PlacesApiClient
import com.atlunch.data.network.SearchNearbyRequest
import com.atlunch.data.network.SearchQueryRequest
import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.PlacesResult
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
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
    ): Flow<PlacesResult> = flow<PlacesResult> {
        val request = SearchNearbyRequest(
            includedTypes = listOf("restaurant"), // hard coded
            maxResultCount = 20, // hard coded
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(40.728480, -73.982142), // TODO this is for Westville
                    radius = 500.0
                )
            )
        )
        val response = apiClient.searchNearby(request)
        val result = response.places.map { placePreviewDTO -> placePreviewDTO.toDomain() }
        emit(PlacesResult.PlacesSuccess(result))
    }.catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }
        emit(throwable.toPlacesDomainError())
    }

    override fun getPlaceDetails(id: String): Flow<PlaceDetailsResult> = flow<PlaceDetailsResult> {
        val response = apiClient.getPlaceDetails(id = id)
        emit(PlaceDetailsResult.DetailsSuccess(response.toDomain()))
    }. catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }
        emit(throwable.toPlaceDetailsDomainError())
    }

    override fun searchQuery(query: String): Flow<PlacesResult> = flow<PlacesResult> {
        val request = SearchQueryRequest(
            textQuery = query,
            includedType = "restaurant", // hard coded
            pageSize = 20, // hard coded, might be token if there are more results
            locationBias = LocationBias(
                circle = Circle(
                    center = LatLng(40.728480, -73.982142), // TODO this is for Westville
                    radius = 500.0
                )
            )
        )
        val response = apiClient.searchQuery(request)
        val result = response.places.map { placePreviewDTO -> placePreviewDTO.toDomain() }
        emit(PlacesResult.PlacesSuccess(result))
    }.catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }
        emit(throwable.toPlacesDomainError())
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindPlacesRepository(placesRepositoryImpl: PlacesRepositoryImpl): PlacesRepository
}
