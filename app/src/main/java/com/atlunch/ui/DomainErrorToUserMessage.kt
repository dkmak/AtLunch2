package com.atlunch.ui

import com.atlunch.domain.DomainError

fun Throwable.toUserMessage(): String =
    when (this) {
        is DomainError.Network ->
            "Please check your internet connection and try again."
        is DomainError.EmptyResult -> {
            "No results were found."
        }
        else ->
            this.message?:"An unknown error occurred."
    }