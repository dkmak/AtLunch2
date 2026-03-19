package com.atlunch.domain

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
        val photos: List<Photo>
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


