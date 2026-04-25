package com.atlunch.domain

import com.atlunch.domain.PlaceDetailsResult.DetailsError

sealed class PlacesResult {
    data class PlacesSuccess(val places: List<PlacePreview>): PlacesResult()
    sealed class PlacesError: PlacesResult() {
        data object Network : PlacesError()
        data object Backend : PlacesError()
        data object Unknown : PlacesError()
    }
}

sealed class PlaceDetailsResult {
    data class DetailsSuccess(
        val placeDetails: PlaceDetails,
        val photos: List<Photo>,
        val favorite: Boolean
    ): PlaceDetailsResult()
    sealed class DetailsError: PlaceDetailsResult() {
        data object Network : DetailsError()
        data object Backend : DetailsError()
        data object Unknown : DetailsError()
    }
}

sealed class LocationResult {
    data class LocationSuccess(
        val location: Location
    ) : LocationResult()

    sealed class LocationError : LocationResult() {
        data object Unknown : LocationError()
    }
}

sealed class FavoriteResult {
    data class FavoriteSuccess(
        val isFavorite: Boolean
    ): FavoriteResult()

    sealed class FavoriteError() : FavoriteResult() {
        data object DatabaseError : FavoriteError()
    }
}

sealed class SummaryResult {
    data class SummarySuccess(
        val isFavorite: Boolean
    ): SummaryResult()

    sealed class SummaryError() : SummaryResult() {
        data object Network : SummaryError()
        data object Backend : SummaryError()
        data object Unknown : SummaryError()
    }
}

