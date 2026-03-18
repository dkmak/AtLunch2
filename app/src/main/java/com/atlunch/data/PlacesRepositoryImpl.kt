package com.atlunch.data

import com.atlunch.data.dto.toDomain
import com.atlunch.data.network.Circle
import com.atlunch.data.network.LatLng
import com.atlunch.data.network.LocationBias
import com.atlunch.data.network.LocationRestriction
import com.atlunch.data.network.PlacesApiClient
import com.atlunch.data.network.SearchNearbyRequest
import com.atlunch.data.network.SearchQueryRequest
import com.atlunch.domain.DomainError
import com.atlunch.domain.PlaceDetails
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
        emit(response.places.map { placePreviewDTO -> placePreviewDTO.toDomain() })
    }.catch { throwable ->
        throw throwable.toDomainError() // TODO I'm not sure how I would never map an error to the viewModel
    }

    override fun getPlaceDetails(id: String): Flow<PlaceDetails> = flow {
        val response = apiClient.getPlaceDetails(id = id)
        emit(response.toDomain())
    }. catch { throwable ->
        throw throwable.toDomainError()
    }

    override fun searchQuery(query: String): Flow<List<PlacePreview>> = flow {
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
        if (response.places.isEmpty()){
            throw DomainError.EmptyResult
        }
        emit(response.places.map { placePreviewDTO -> placePreviewDTO.toDomain() })
    }.catch { throwable ->
        throw throwable.toDomainError()
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindPlacesRepository(placesRepositoryImpl: PlacesRepositoryImpl): PlacesRepository
}