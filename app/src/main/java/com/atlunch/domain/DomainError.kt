package com.atlunch.domain

sealed class DomainError: Throwable() {
    object Network : DomainError()
    object EmptyResult : DomainError()
    data class Unknown(override val cause: Throwable) : DomainError()
}