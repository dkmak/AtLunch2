package com.atlunch.ui

import com.atlunch.domain.LocationResult
import com.atlunch.domain.PlaceDetailsResult
import com.atlunch.domain.PlacesResult

fun PlacesResult.PlacesError.toUserMessage(): String =
    when (this) {
        is PlacesResult.PlacesError.Network ->
            "Please check your internet connection and try again."
        is PlacesResult.PlacesError.Backend ->
            "We're having trouble reaching the Google API servers right now. Please try again in a moment."
        is PlacesResult.PlacesError.Unknown ->
            "An unknown error occurred."
    }

fun PlaceDetailsResult.DetailsError.toUserMessage(): String =
    when (this) {
        is PlaceDetailsResult.DetailsError.Network ->
            "Please check your internet connection and try again."
        is PlaceDetailsResult.DetailsError.Backend ->
            "We're having trouble reaching Google API servers right now. Please try again in a moment."
        is PlaceDetailsResult.DetailsError.Unknown ->
            "An unknown error occurred."
    }

fun LocationResult.LocationError.toUserMessage(): String =
    when (this) {
        LocationResult.LocationError.Unknown -> "We couldn't determine your current location."
    }
