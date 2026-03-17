package com.atlunch.data

import com.atlunch.domain.DomainError
import okio.IOException

fun Throwable.toDomainError(): DomainError {
    return when (this) {
        is IOException -> DomainError.Network
        is DomainError.EmptyResult -> {this}
        else -> DomainError.Unknown(this)
    }
}