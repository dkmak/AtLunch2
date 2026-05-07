package dkmak.atlunch

import dkmak.atlunch.domain.FavoriteResult
import dkmak.atlunch.domain.PlaceDetailsResult
import dkmak.atlunch.domain.PlacesRepository
import dkmak.atlunch.domain.PlacesResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePlacesRepository : PlacesRepository {
    var placeDetailsResult: PlaceDetailsResult = PlaceDetailsResult.DetailsError.Unknown
    var nearbyResult: PlacesResult = PlacesResult.PlacesError.Unknown
    var queryResult: PlacesResult = PlacesResult.PlacesError.Unknown
    var favoriteResult: FavoriteResult = FavoriteResult.FavoriteError.DatabaseError

    var lastRequestedPlaceId: String? = null
        private set
    var lastSearchQuery: String? = null
        private set
    var lastNearbyLat: Double? = null
        private set
    var lastNearbyLong: Double? = null
        private set
    var lastFavoritePlaceId: String? = null
        private set

    override fun searchNearby(
        lat: Double,
        long: Double,
    ): Flow<PlacesResult> {
        lastNearbyLat = lat
        lastNearbyLong = long
        return flowOf(nearbyResult)
    }

    override fun getPlaceDetails(id: String): Flow<PlaceDetailsResult> {
        lastRequestedPlaceId = id
        return flowOf(placeDetailsResult)
    }

    override fun searchQuery(
        query: String,
        lat: Double,
        long: Double,
    ): Flow<PlacesResult> {
        lastSearchQuery = query
        lastNearbyLat = lat
        lastNearbyLong = long
        return flowOf(queryResult)
    }

    override fun addFavorite(id: String): Flow<FavoriteResult> {
        lastFavoritePlaceId = id
        return flowOf(FavoriteResult.FavoriteSuccess(isFavorite = true))
    }

    override fun removeFavorite(id: String): Flow<FavoriteResult> {
        lastFavoritePlaceId = id
        return flowOf(FavoriteResult.FavoriteSuccess(isFavorite = false))
    }
}
