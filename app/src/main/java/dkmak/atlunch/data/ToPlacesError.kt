package dkmak.atlunch.data

import dkmak.atlunch.domain.PlaceDetailsResult
import dkmak.atlunch.domain.PlacesResult
import dkmak.atlunch.domain.SummaryResult
import dkmak.atlunch.domain.SummaryResult.SummaryError
import okio.IOException
import retrofit2.HttpException

fun Throwable.toPlacesDomainError(): PlacesResult =
    when (this) {
        is IOException -> PlacesResult.PlacesError.Network
        is HttpException -> PlacesResult.PlacesError.Backend
        else -> PlacesResult.PlacesError.Unknown
    }

fun Throwable.toPlaceDetailsDomainError(): PlaceDetailsResult =
    when (this) {
        is IOException -> PlaceDetailsResult.DetailsError.Network
        is HttpException -> PlaceDetailsResult.DetailsError.Backend
        else -> PlaceDetailsResult.DetailsError.Unknown
    }

fun Throwable.toSummaryDomainError(): SummaryError =
    when (this) {
        is IOException -> SummaryError.Network
        is HttpException -> SummaryResult.SummaryError.Backend
        else -> SummaryResult.SummaryError.Unknown
    }
