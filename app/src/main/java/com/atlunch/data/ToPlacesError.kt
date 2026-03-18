package com.atlunch.data

import com.atlunch.data.dto.toDomain
import com.atlunch.domain.DomainError
import com.atlunch.domain.PlacePreview
import com.atlunch.domain.PlacesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

fun Throwable.toDomainError(): DomainError {
    return when (this) {
        is IOException -> DomainError.Network
        is DomainError.EmptyResult -> {this}
        else -> DomainError.Unknown(this)
    }
}