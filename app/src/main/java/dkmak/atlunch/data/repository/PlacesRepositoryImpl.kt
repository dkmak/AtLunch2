package dkmak.atlunch.data.repository

import dkmak.atlunch.data.IoDispatcher
import dkmak.atlunch.data.database.FavoritesEntity
import dkmak.atlunch.data.database.PlacesDAO
import dkmak.atlunch.data.dto.toDomain
import dkmak.atlunch.data.dto.toEntity
import dkmak.atlunch.data.network.places.Circle
import dkmak.atlunch.data.network.places.LatLngDTO
import dkmak.atlunch.data.network.places.LocationBias
import dkmak.atlunch.data.network.places.LocationRestriction
import dkmak.atlunch.data.network.places.PlacesApiClient
import dkmak.atlunch.data.network.places.SearchNearbyRequest
import dkmak.atlunch.data.network.places.SearchQueryRequest
import dkmak.atlunch.data.toPlaceDetailsDomainError
import dkmak.atlunch.data.toPlacesDomainError
import dkmak.atlunch.domain.FavoriteResult
import dkmak.atlunch.domain.PlaceDetailsResult
import dkmak.atlunch.domain.PlacePreview
import dkmak.atlunch.domain.PlacesRepository
import dkmak.atlunch.domain.PlacesResult
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

class PlacesRepositoryImpl
    @Inject
    constructor(
        private val apiClient: PlacesApiClient,
        private val placesDAO: PlacesDAO,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : PlacesRepository {
        override fun searchNearby(
            lat: Double,
            long: Double,
        ): Flow<PlacesResult> =
            flow {
                val request =
                    SearchNearbyRequest(
                        includedTypes = listOf(INCLUDED_TYPE),
                        maxResultCount = MAX_RESULTS,
                        locationRestriction =
                            LocationRestriction(
                                circle =
                                    Circle(
                                        center = LatLngDTO(lat, long),
                                        radius = MAX_RADIUS,
                                    ),
                            ),
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
                        },
                    )
                    return@flow
                }

                emit(PlacesResult.PlacesSuccess(getCachedNearbyPlaces()))
            }.flowOn(ioDispatcher)

        private suspend fun getCachedNearbyPlaces(): List<PlacePreview> = placesDAO.getPlacePreviews().map { it.toDomain() }

        override fun getPlaceDetails(id: String): Flow<PlaceDetailsResult> =
            flow<PlaceDetailsResult> {
                val detailsResponse = apiClient.getPlaceDetails(id = id)

                val photoResources = detailsResponse.photos.take(MAX_PHOTOS)
                val photosDomain =
                    coroutineScope {
                        photoResources.map { photoResource ->
                            async {
                                apiClient.getPhotos(photoResource.name).toDomain()
                            }
                        }
                    }

                val placeDetails = detailsResponse.toDomain()
                // read the data, find if item in Favorites table
                val isFavorite = placesDAO.getFavorites().find { it.id == placeDetails.id }

                emit(
                    PlaceDetailsResult.DetailsSuccess(
                        placeDetails = detailsResponse.toDomain(),
                        photos = photosDomain.awaitAll(),
                        favorite = isFavorite != null,
                    ),
                )
            }.catch { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                emit(throwable.toPlaceDetailsDomainError())
            }.flowOn(ioDispatcher)

        override fun searchQuery(
            query: String,
            lat: Double,
            long: Double,
        ): Flow<PlacesResult> =
            flow<PlacesResult> {
                val request =
                    SearchQueryRequest(
                        textQuery = query,
                        includedType = INCLUDED_TYPE,
                        pageSize = MAX_RESULTS,
                        locationBias =
                            LocationBias(
                                circle =
                                    Circle(
                                        center = LatLngDTO(lat, long),
                                        radius = MAX_RADIUS,
                                    ),
                            ),
                    )
                val response = apiClient.searchQuery(request)
                val result = response.places.map { placePreviewDTO -> placePreviewDTO.toDomain() }
                emit(PlacesResult.PlacesSuccess(result))
            }.catch { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                emit(throwable.toPlacesDomainError())
            }.flowOn(ioDispatcher)

        override fun addFavorite(id: String): Flow<FavoriteResult> =
            flow<FavoriteResult> {
                placesDAO.insertFavorite(FavoritesEntity(id))
                emit(FavoriteResult.FavoriteSuccess(isFavorite = true))
            }.catch {
                emit(FavoriteResult.FavoriteError.DatabaseError)
            }

        override fun removeFavorite(id: String): Flow<FavoriteResult> =
            flow<FavoriteResult> {
                placesDAO.deleteFavorites(id)
                emit(FavoriteResult.FavoriteSuccess(isFavorite = false))
            }.catch {
                emit(FavoriteResult.FavoriteError.DatabaseError)
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
internal abstract class PlacesRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlacesRepository(placesRepositoryImpl: PlacesRepositoryImpl): PlacesRepository
}
