package com.atlunch.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotoMediaDTO(
    val name: String,
    val photoUri: String
)
