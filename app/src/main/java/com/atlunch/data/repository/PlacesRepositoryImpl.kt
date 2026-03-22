package com.atlunch.data.repository

import com.atlunch.data.IoDispatcher
import com.atlunch.data.database.PlacesDAO
import com.atlunch.data.dto.toDomain
import com.atlunch.data.dto.toEntity
import com.atlunch.data.network.Circle
import com.atlunch.data.network.LatLngDTO
import com.atlunch.data.network.LocationBias
import com.atlunch.data.network.LocationRestriction
import com.atlunch.data.network.PlacesApiClient
import com.atlunch.data.network.SearchNearbyRequest
import com.atlunch.data.network.SearchQueryRequest
import com.atlunch.data.toPlaceDetailsDomainError
import com.atlunch.data.toPlacesDomainError
import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.PlacesResult
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

class PlacesRepositoryImpl @Inject constructor(
    private val apiClient: PlacesApiClient,
    private val placesDAO: PlacesDAO,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PlacesRepository {

    override fun searchNearby(
        lat: Double,
        long: Double
    ): Flow<PlacesResult> = flow {
        val request = SearchNearbyRequest(
            includedTypes = listOf(INCLUDED_TYPE),
            maxResultCount = MAX_RESULTS,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLngDTO(lat, long),
                    radius = MAX_RADIUS
                )
            )
        )

        try {
            val response = apiClient.searchNearby(request)
            placesDAO.clearPlacePreviews()
            placesDAO.insertPlacePreviews(response.places.map { it.toEntity() })
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable

            val cachedPlaces = getCachedNearbyPlaces()
            emit(
                if (cachedPlaces.isNotEmpty()) {
                    PlacesResult.PlacesSuccess(cachedPlaces)
                } else {
                    throwable.toPlacesDomainError()
                }
            )
            return@flow
        }

        emit(PlacesResult.PlacesSuccess(getCachedNearbyPlaces()))
    }.flowOn(ioDispatcher)

    private suspend fun getCachedNearbyPlaces(): List<PlacePreview> {
        return placesDAO.getPlacePreviews().map { it.toDomain() }
    }


    override fun getPlaceDetails(id: String): Flow<PlaceDetailsResult> = flow<PlaceDetailsResult> {
        val detailsResponse = apiClient.getPlaceDetails(id = id)

        val photoResources = detailsResponse.photos.take(MAX_PHOTOS)
        val photosDomain = coroutineScope {
            photoResources.map { photoResource ->
                async {
                    apiClient.getPhotos(photoResource.name).toDomain()
                }
            }
        }
        emit(PlaceDetailsResult.DetailsSuccess(
            placeDetails = detailsResponse.toDomain(),
            photos = photosDomain.awaitAll()
        ))
    }. catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }
        emit(throwable.toPlaceDetailsDomainError())
    }.flowOn(ioDispatcher)

    override fun searchQuery(
        query: String,
        lat: Double,
        long: Double
    ): Flow<PlacesResult> = flow<PlacesResult> {
        val request = SearchQueryRequest(
            textQuery = query,
            includedType = INCLUDED_TYPE,
            pageSize = MAX_RESULTS,
            locationBias = LocationBias(
                circle = Circle(
                    center = LatLngDTO(lat, long),
                    radius = MAX_RADIUS
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
    }.flowOn(ioDispatcher)

    companion object {
        const val MAX_PHOTOS = 6
        const val MAX_RESULTS = 20
        const val MAX_RADIUS = 1000.0
        const val INCLUDED_TYPE = "restaurant"
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindPlacesRepository(placesRepositoryImpl: PlacesRepositoryImpl): PlacesRepository
}
