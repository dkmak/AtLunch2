package com.atlunch.data

import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesResult
import okio.IOException
import retrofit2.HttpException

fun Throwable.toPlacesDomainError(): PlacesResult {
    return when (this) {
        is IOException -> PlacesResult.PlacesError.Network
        is HttpException -> PlacesResult.PlacesError.Backend
        else -> PlacesResult.PlacesError.Unknown
    }
}

fun Throwable.toPlaceDetailsDomainError(): PlaceDetailsResult {
    return when (this) {
        is IOException -> PlaceDetailsResult.DetailsError.Network
        is HttpException -> PlaceDetailsResult.DetailsError.Backend
        else -> PlaceDetailsResult.DetailsError.Unknown
    }
}
