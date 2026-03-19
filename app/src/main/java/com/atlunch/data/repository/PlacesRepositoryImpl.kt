package com.atlunch.data.repository

import com.atlunch.data.dto.toDomain
import com.atlunch.data.network.Circle
import com.atlunch.data.network.LatLng
import com.atlunch.data.network.LocationBias
import com.atlunch.data.network.LocationRestriction
import com.atlunch.data.network.PlacesApiClient
import com.atlunch.data.network.SearchNearbyRequest
import com.atlunch.data.network.SearchQueryRequest
import com.atlunch.data.toPlaceDetailsDomainError
import com.atlunch.data.toPlacesDomainError
import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesRepository
import com.atlunch.domain.PlacesResult
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
            includedTypes = listOf(INCLUDED_TYPE),
            maxResultCount = MAX_RESULTS,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(lat, long),
                    radius = MAX_RADIUS
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
        val detailsResponse = apiClient.getPlaceDetails(id = id)


        // TODO this is one-by-one
/*        val photos = detailsResponse.photos.take(MAX_PHOTOS)
        val photosDomain = photos.map { photoResource ->
            val photoResourceResponse = apiClient.getPhotos(photoResource.name)
            photoResourceResponse.toDomain()
        }*/

        val photoResources = detailsResponse.photos.take(MAX_PHOTOS)
        val photosDomain = coroutineScope {
            photoResources.map { photoResource ->
                async {
                    apiClient.getPhotos(photoResource.name).toDomain()
                }
            }.awaitAll()
        }
        emit(PlaceDetailsResult.DetailsSuccess(
            placeDetails = detailsResponse.toDomain(),
            photos = photosDomain
        ))
    }. catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }
        emit(throwable.toPlaceDetailsDomainError())
    }

    override fun searchQuery(
        query: String
    ): Flow<PlacesResult> = flow<PlacesResult> {
        val request = SearchQueryRequest(
            textQuery = query,
            includedType = INCLUDED_TYPE, // hard coded
            pageSize = MAX_RESULTS, // hard coded, might be token if there are more results
            locationBias = LocationBias(
                circle = Circle(
                    center = LatLng(40.728480, -73.982142), // TODO this is for Westville
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
    }

    companion object {
        const val MAX_PHOTOS = 8
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
